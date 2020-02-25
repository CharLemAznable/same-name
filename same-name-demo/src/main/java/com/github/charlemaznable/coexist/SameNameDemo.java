package com.github.charlemaznable.coexist;

import lombok.val;

public class SameNameDemo {

    public static void main(String[] args) {
        val sameNameWrapper = new SameNameWrapper();
        System.out.println(sameNameWrapper.descriptionV1());
        System.out.println(sameNameWrapper.descriptionV2());
    }
}
