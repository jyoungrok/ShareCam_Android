package com.claude.sharecam.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by Claude on 15. 7. 9..
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
//    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
//    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private StaggeredGridLayoutManager mLayoutManager;

    public EndlessRecyclerOnScrollListener(StaggeredGridLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int[] firstVisibleItems = null;
        firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);

        if (!loading) {
            if ((visibleItemCount + firstVisibleItems[0]) >= totalItemCount) {

                Log.d("tag", "LOAD NEXT ITEM");

//다음 페이지 호출
                onLoadMore(current_page+1);

            }
        }
    }

    //로딩 시작시 onLoadMore에서 호출
    public void startLoading()
    {
        this.loading=true;
    }

    //로딩 종료시 onLoadMore에서 호출
    public void finishLoading()
    {
        current_page++;
        loading=false;
    }

    public abstract void onLoadMore(int current_page);
}