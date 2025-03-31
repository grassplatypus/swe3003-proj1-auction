create or replace function check_buy_status(var_buyer_id in varchar(30))
    returns table (
        item_id int,
        item_desc varchar(30),
        highest_bidder varchar(30),
        highest_bidding_price numeric,
        my_bid_price numeric,
        bid_closing_time timestamp
                  ) as $$
declare
    item_data int[]; -- 사용자가 입찰중인 item 저장
    -- Loop용 임시 변수들
    current_item_id int;
    current_auction_id int;
    current_item_desc varchar(1000);
    current_bid_end_time timestamp;
    highest_bidder_username varchar(30);
    highest_bid_price numeric;
begin
    begin
        -- 사용자가 참여중/참여중이었던 bids의 아이템 모두 모으기
        select array_agg(item_id)
        into item_data
        from auctions join bids using (auction_id)
        where bidder_id = var_buyer_id
          and bid_status <> 'won'
          and (auctions.bid_end_time > now()
                or auctions.auction_status in ('sold', 'expired')); -- 이미 딴 것과 종료된 것 제외
        exception
        when NO_DATA_FOUND then -- 항목 못찾으면 예외처리
            return 'failed';
    end;
    -- 각 item_data에 대해 최상위 입찰자 찾기
    foreach current_item_id in array item_data loop -- foreach로 각 참여중인 item id로 iterate
        -- 각 아이템마다 경매 조회
        select auction_id, item_desc, bid_end_time
        into current_auction_id, current_item_desc, current_bid_end_time
        from items join auctions using (auction_id)
        where items.item_id = current_item_id;

        -- bids 테이블에서 최고 입찰자 찾기
        select bidder_id, bid_price
        into highest_bidder_username, highest_bid_price
        from bids
        where auction_id = current_auction_id
        order by bid_price desc;

        -- TODO: 사용자 정보와 비교

    end loop;

end $$
    language plpgsql;