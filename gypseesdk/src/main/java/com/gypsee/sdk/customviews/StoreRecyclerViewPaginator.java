package com.gypsee.sdk.customviews;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class StoreRecyclerViewPaginator extends RecyclerView.OnScrollListener {

    private int batchSize = 8;
    private int currentPage = 0;
    private int threshold = 2;



    private RecyclerView.LayoutManager layoutManager;

    public StoreRecyclerViewPaginator(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(this);
        this.layoutManager = recyclerView.getLayoutManager();
    }


    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING){

            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();

            int firstVisibleItemPosition = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();
            if (!canLoadNext())return;
            if (isLastCategory() && isLastPage()){
                return;
            }
            if ((visibleItemCount + firstVisibleItemPosition + threshold) >= totalItemCount){
                    if (isFirstCategory()){
                        loadMore(batchSize*(currentPage+1), batchSize);
                    }else {
                        loadMore((batchSize*currentPage), batchSize);
                    }
                    currentPage += 1;
            }
        }
    }

    private String TAG = StoreRecyclerViewPaginator.class.getSimpleName();





    public void resetCurrentPage(){
        this.currentPage = 0;
    }


    public abstract boolean canLoadNext();

    public abstract boolean isFirstCategory();
    public abstract boolean isLastPage();
    public abstract boolean isLastCategory();
    public abstract void loadMore(int offset, int limit);


}
