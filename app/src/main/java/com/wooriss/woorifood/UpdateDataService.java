package com.wooriss.woorifood;

import java.util.List;

public interface UpdateDataService {
    void updateUserInfo(String nBranchName,String nBranchAddr, String nUserPosition);
//    void insertReview(Sikdang sikdang);
    void getReviewedSikdangs(String x, String y, int innerMeter);

}
