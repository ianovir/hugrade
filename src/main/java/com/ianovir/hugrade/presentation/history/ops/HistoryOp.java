package com.ianovir.hugrade.presentation.history.ops;

public interface HistoryOp<T> {

    void onDo(T graph);
    void onUndo(T graph);

}
