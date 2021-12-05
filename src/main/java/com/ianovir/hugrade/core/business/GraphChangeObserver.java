package com.ianovir.hugrade.core.business;

import com.ianovir.hugrade.presentation.views.GraphView;

public interface GraphChangeObserver {
    void onGraphChanged(GraphView gv);
}