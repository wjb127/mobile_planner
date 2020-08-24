package org.techtown.planner01;

import android.view.View;

// 노트아이템 클릭 받기 : 아이템 클릭
public interface OnNoteItemClickListener {
    public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
