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
    item_data int[];

begin
    -- 사용자가 참여중/참여중이었던 bids의 아이템 모두 모으기
    select array_agg(item_id)
    into item_data
    from auctions join bids using (auction_id)
    where bidder_id = var_buyer_id
      and bid_status <> 'won'
      and (auctions.bid_end_time > now()
            or auctions.auction_status in ('sold', 'expired')); -- 이미 딴 것과 종료된 것 제외

    -- 각 item_data에 대해 최상위 입찰자 찾기
    for
    select bidder_id, bid_price from bids where auction_id = ;

end $$
    language plpgsql;