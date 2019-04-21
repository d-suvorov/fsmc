contract Contract {
  uint private creationTime = now;

  enum State {
    ST_0, ST_1, ST_2, ST_3, ST_4
  }
  State private state = States.ST_0;


  function use() public {
    require(state == States.ST_0 || state == States.ST_1 || state == States.ST_2 || state == States.ST_4)
    if (state == ST_0) {
      _terminate_action();
      state = ST_3;
    }
    if (state == ST_1) {
      state = ST_1;
    }
    if (state == ST_2) {
      state = ST_2;
    }
    if (state == ST_4) {
      state = ST_4;
    }
  }

  function getLicense() public {
    require(state == States.ST_0)
    if (state == ST_0) {
      state = ST_1;
    }
  }

  function publish() public {
    require(state == States.ST_0 || state == States.ST_1 || state == States.ST_2 || state == States.ST_3)
    if (state == ST_0) {
      _terminate_action();
      state = ST_3;
    }
    if (state == ST_1) {
      state = ST_4;
    }
    if (state == ST_2) {
      state = ST_2;
    }
    if (state == ST_3) {
      _terminate_action();
      state = ST_3;
    }
  }

  function getApproval() public {
    require(state == States.ST_1)
    if (state == ST_1) {
      state = ST_2;
    }
  }

  function noRemove() public {
    require(state == States.ST_4)
    if (state == ST_4) {
      _terminate_action();
      state = ST_3;
    }
  }

  function remove() public {
    require(state == States.ST_4)
    if (state == ST_4) {
      state = ST_1;
    }
  }

  function _terminate_action() public {
    emit Terminate;
  }
}
