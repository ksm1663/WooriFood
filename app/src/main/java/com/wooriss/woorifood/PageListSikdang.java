package com.wooriss.woorifood;

import java.io.Serializable;
import java.util.List;

public class PageListSikdang {
    PlaceMeta meta;
    List<Sikdang> documents;

    public PlaceMeta getMeta() {
        return meta;
    }

    public void setMeta(PlaceMeta meta) {
        this.meta = meta;
    }

    public List<Sikdang> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Sikdang> documents) {
        this.documents = documents;
    }
}

class PlaceMeta{
    int total_count;            // 검색어에 검색된 문서 수
    int pageable_count;        // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)
    Boolean is_end;             // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
    RegionInfo same_name;          // 질의어의 지역 및 키워드 분석 정보
}

class RegionInfo {
    List<String> region;     // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    String keyword;  // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'
    String selected_region;   // 인식된 지역 리스트 중, 현재 검색
}

class Sikdang implements Serializable, Comparable<Sikdang> {
    private String id;           // 장소 ID
    private String place_name;    // 장소명, 업체명
    private String category_name;   // 카테고리 이름
    private String category_group_code;    // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    private String category_group_name;   // 중요 카테고리만 그룹핑한 카테고리 그룹명
    private String phone;       // 전화번호
    private String address_name;   // 전체 지번 주소
    private String road_address_name;    // 전체 도로명 주소
    private String x;         // X 좌표값 혹은 longitude
    private String y;            // Y 좌표값 혹은 latitude
    private String place_url;    // 장소 상세페이지 URL
    private String distance;      // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter


    // 리뷰 관련 항목들
    private double avgTaste;
    private float avgPrice;
    private float avgVisit;
    private float avgLuxury;

    private float avgFirstComplex;
    private float avgSecondComplex;
    private float avgThirdComplex;
    private int numFirstComplex;
    private int numSecondComplex;
    private int numThirdComplex;

    private int numRatings;
    private int viewType;

    private String titleImage;



    public Sikdang(){}

    public String getAddress_name() {
        return address_name;
    }

    public String getCategory_group_code() {
        return category_group_code;
    }

    public String getCategory_group_name() {
        return category_group_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDistance() {
        return distance;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getPlace_url() {
        return place_url;
    }

    public String getRoad_address_name() {
        return road_address_name;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public double getAvgTaste() {
        return avgTaste;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public int getViewType() {
        return viewType;
    }

    public void setAvgTaste(double avgTaste) {
        this.avgTaste = avgTaste;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public float getAvgPrice() {
        return avgPrice;
    }

    public float getAvgVisit() {
        return avgVisit;
    }

    public float getAvgFirstComplex() {
        return avgFirstComplex;
    }

    public float getAvgSecondComplex() {
        return avgSecondComplex;
    }

    public float getAvgThirdComplex() {
        return avgThirdComplex;
    }

    public void setAvgPrice(float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public void setAvgVisit(float avgVisit) {
        this.avgVisit = avgVisit;
    }

    public void setAvgFirstComplex(float avgFirstComplex) {
        this.avgFirstComplex = avgFirstComplex;
    }

    public void setAvgSecondComplex(float avgSecondComplex) {
        this.avgSecondComplex = avgSecondComplex;
    }

    public void setAvgThirdComplex(float avgThirdComplex) {
        this.avgThirdComplex = avgThirdComplex;
    }

    public int getNumFirstComplex() {
        return numFirstComplex;
    }

    public int getNumSecondComplex() {
        return numSecondComplex;
    }

    public int getNumThirdComplex() {
        return numThirdComplex;
    }

    public void setNumFirstComplex(int numFirstComplex) {
        this.numFirstComplex = numFirstComplex;
    }

    public void setNumSecondComplex(int numSecondComplex) {
        this.numSecondComplex = numSecondComplex;
    }

    public void setNumThirdComplex(int numThirdComplex) {
        this.numThirdComplex = numThirdComplex;
    }

    public float getAvgLuxury() {
        return avgLuxury;
    }

    public void setAvgLuxury(float avgLuxury) {
        this.avgLuxury = avgLuxury;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    @Override
    public int compareTo(Sikdang sikdang) {
        int distance = Integer.parseInt(sikdang.getDistance());
        return Integer.parseInt(this.distance) - distance ;
        //sikdang.getDistance().compareTo(this.distance);
    }

}


