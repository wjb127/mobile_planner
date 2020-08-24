package org.techtown.planner01;

// 탭 선택 입력받기 : 탭 선택(포지션 정보), 프래그먼트2 보이기
public interface OnTabItemSelectedListener {
    public void onTabSelected(int position);
    public void showFragment2(Note item);
}
