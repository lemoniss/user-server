package com.maxlength.spec.common;

import lombok.Getter;

// test
@Getter
public class Offset {
    private int page;
    private int limit;

    public Offset(int offset, int limit){
        this.limit = limit;
        if(this.limit == 0){
            this.page = 0;
        } else {
            this.page = (int) Math.floor((offset + 1) / limit );
        }
    }
}
