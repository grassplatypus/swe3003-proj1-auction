create or replace function buy_item(var_item_id in int, var_buyer_id in varchar(30), var_price in numeric)
    returns varchar(30) as $$
    declare
        item_buy record;

    begin
        select a.auction_id, a.buy_it_now_price, a.current_price, a.auction_status, a.bid_end_time, i.seller_id
        into item_buy
        from items i join auctions a using (auction_id)
        where i.item_id = var_item_id and a.bid_end_time > now()
        for update;
        if var_price >= item_buy.buy_it_now_price and item_buy.auction_status in ('listed', 'bidding') then
            -- 구매한 값이 current_price가 됨
            update auctions
            set auction_status = 'sold', current_price = var_price
            where auction_id = item_buy.auction_id;

            -- Bid 목록 추가
            update bids
            set bid_status = 'outbid'
            where auction_id = item_buy.auction_id;

            -- 기존 경매 입찰자 전부 무효 처리
            insert into billing (item_id, buyer_id, seller_id, final_price, transaction_time, payment_status)
            values (var_item_id, var_buyer_id, item_buy.seller_id, var_price, 'completed');
            commit;
            return 'purchased';
        elseif var_price > item_buy.current_price and item_buy.auction_status in ('listed', 'bidding') then
            -- 최고 값 갱신
            update auctions
            set current_price = var_price
            where auction_id = item_buy.auction_id;

            -- Bid 목록 추가
            insert into bids (bidder_id, auction_id, bid_price, bid_status)
            values (var_buyer_id, item_buy.auction_id, var_price, 'active');

            -- 기존 낮은 값 제시자 전부 무효 처리
            update bids
            set bid_status = 'outbid'
            where auction_id = item_buy.auction_id;
            commit;
            return 'bid-ok';
        else
            rollback;
            return 'failed';
        end if;
    end $$
    language plpgsql;