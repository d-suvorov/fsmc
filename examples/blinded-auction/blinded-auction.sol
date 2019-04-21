contract Contract {
  uint private creationTime = now;

  enum State {
    ST_0, ST_1, ST_2, ST_3
  }
  State private state = States.ST_0;

  struct Bid {
    bytes32 blindedBid;
    uint deposit;
  }
  
  mapping(address => Bid[]) private bids;
  mapping(address => uint)  private pendingReturns;
  address private highestBidder;
  uint private highestBid;
  boolean private closed = false;
  
  function biddingOver() private returns (bool) {
    return now > creationTime + 5 days;
  }
  
  function revealOver() private returns (bool) {
    return now >= creationTime + 10 days;
  }

  function cancel() public {
    require(state == States.ST_0 || state == States.ST_2)
    if (state == ST_0) {
      cancel();
      state = ST_3;
    }
    if (state == ST_2) {
      cancel();
      state = ST_3;
    }
  }

  function bid() public {
    require(state == States.ST_0)
    if (state == ST_0) {
      bid();
      state = ST_0;
    }
  }

  function close() public {
    require(state == States.ST_0)
    if (state == ST_0) {
      close();
      state = ST_2;
    }
  }

  function withdraw() public {
    require(state == States.ST_1)
    if (state == ST_1) {
      withdraw();
      state = ST_1;
    }
  }

  function reveal() public {
    require(state == States.ST_2)
    if (state == ST_2) {
      reveal();
      state = ST_2;
    }
  }

  function finish() public {
    require(state == States.ST_2)
    if (state == ST_2) {
      finish();
      state = ST_1;
    }
  }

  function unbid() public {
    require(state == States.ST_3)
    if (state == ST_3) {
      unbid();
      state = ST_3;
    }
  }

  function bid() public {
    bids[msg.sender].push(Bid({
      blindedBid: blindedBid,
      deposit: msg.value
    }));
    pendingReturns[msg.sender] += msg.value;
  }

  function close() public {
  }

  function reveal() public {
    for (uint i = 0; i < (bids[msg.sender].length <
        values.length ? bids[msg.sender].length : values.length); i++) {
      var bid = bids[msg.sender][i];
      var (value, secret) = (values[i], secrets[i]);
      if (bid.blindedBid == keccak256(value, secret) &&
          bid.deposit >= value && value > highestBid) {
        highestBid = value;
        highestBidder = msg.sender;
      }
    }
  }

  function finish() public {
  }

  function withdraw() public {
    uint amount = pendingReturns[msg.sender];
    if (amount > 0) {
      if (msg.sender!= highestBidder)
        msg.sender.transfer(amount);
      else
        msg.sender.transfer(amount - highestBid);
      pendingReturns[msg.sender] = 0;
    }
  }

  function cancel() public {
  }

  function unbid() public {
    uint amount = pendingReturns[msg.sender];
    if (amount > 0) {
      msg.sender.transfer(amount);
      pendingReturns[msg.sender] = 0;
    }
  }
}
