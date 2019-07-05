package com.redefine.commonui.loadmore.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.utils.LogUtil;

public abstract class HeaderAndFooterRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, H, T, F>
        extends RecyclerView.Adapter<VH> {

    protected static final int TYPE_HEADER = -100;
    protected static final int TYPE_FOOTER = -200;

    private H header;
    private F footer;
    private boolean showFooter = true;
    protected LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;
    private OnFooterItemClickListener onFooterItemClickListener;
    private OnHeaderItemClickListener onHeaderItemClickListener;
    private boolean showHeader = true;

    /**
     * Invokes onCreateHeaderViewHolder, onCreateItemViewHolder or onCreateFooterViewHolder methods
     * based on the view type param.
     */
    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        VH viewHolder;
        if (isHeaderType(viewType)) {
            viewHolder = onCreateHeaderViewHolder(parent, viewType);
        } else if (isFooterType(viewType)) {
            viewHolder = onCreateFooterViewHolder(parent, viewType);
        } else {
            viewHolder = onCreateItemViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected VH onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    /**
     * If you don't need footer feature, you can bypass overriding this method.
     */
    protected VH onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * Invokes onBindHeaderViewHolder, onBindItemViewHolder or onBindFooterViewHOlder methods based
     * on the position param.
     */
    @Override
    public final void onBindViewHolder(final VH holder, int position) {
        if (isHeaderPosition(position)) {
            onBindHeaderViewHolder(holder, position);
            if (onHeaderItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onHeaderItemClickListener.onHeaderItemClick(holder, getHeader());
                    }
                });
            }
        } else if (isFooterPosition(position)) {
            onBindFooterViewHolder(holder, position);
            if (onFooterItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFooterItemClickListener.onFooterItemClick(holder, getFooter());
                    }
                });
            }
        } else {
            if (hasHeader()) {
                position--;
            }
            onBindItemViewHolder(holder, position);
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int p  = holder.getAdapterPosition();

                        if (hasHeader()) {
                            p--;
                        }
                        onItemClickListener.onItemClick(holder, getRealItem(p));
                    }
                });
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    public void setOnFooterItemClickListener(OnFooterItemClickListener clickListener) {
        onFooterItemClickListener = clickListener;
    }

    public void setOnHeaderItemClickListener(OnHeaderItemClickListener clickListener) {
        onHeaderItemClickListener = clickListener;
    }

    /**
     * If you don't need header feature, you can bypass overriding this method.
     */
    protected void onBindHeaderViewHolder(VH holder, int position) {
    }

    protected abstract void onBindItemViewHolder(VH holder, int position);

    /**
     * If you don't need footer feature, you can bypass overriding this method.
     */
    protected void onBindFooterViewHolder(VH holder, int position) {

    }

    /**
     * Invokes onHeaderViewRecycled, onItemViewRecycled or onFooterViewRecycled methods based
     * on the holder.getAdapterPosition()
     */
    @Override
    public final void onViewRecycled(VH holder) {
        int position = holder.getAdapterPosition();

        if (isHeaderPosition(position)) {
            onHeaderViewRecycled(holder);
        } else if (isFooterPosition(position)) {
            onFooterViewRecycled(holder);
        } else {
            onItemViewRecycled(holder);
        }
    }

    protected void onHeaderViewRecycled(VH holder) {
    }

    protected void onItemViewRecycled(VH holder) {
    }

    protected void onFooterViewRecycled(VH holder) {
    }

    /**
     * Returns the type associated to an item given a position passed as arguments. If the position
     * is related to a header item returns the constant TYPE_HEADER or TYPE_FOOTER if the position is
     * related to the footer, if not, returns TYPE_ITEM.
     * <p>
     * If your application has to support different types override this method and provide your
     * implementation. Remember that TYPE_HEADER, TYPE_ITEM and TYPE_FOOTER are internal constants
     * can be used to identify an item given a position, try to use different values in your
     * application.
     */
    @Override
    public final int getItemViewType(int position) {
        int viewType;
        if (isHeaderPosition(position)) {
            viewType = TYPE_HEADER;
        } else if (isFooterPosition(position)) {
            viewType = TYPE_FOOTER;
        } else {
            if (hasHeader()) {
                position--;
            }
            viewType = getRealItemViewType(position);
        }
        return viewType;
    }

    protected abstract int getRealItemViewType(int position);

    /**
     * Returns the items list size if there is no a header configured or the size taking into account
     * that if a header or a footer is configured the number of items returned is going to include
     * this elements.
     */
    @Override
    public final int getItemCount() {
        int size = getRealItemCount();
        if (hasHeader()) {
            size++;
        }
        if (hasFooter()) {
            size++;
        }
        return size;
    }

    public abstract int getRealItemCount();

    /**
     * Get header data in this adapter, you should previously use {@link #setHeader(H header)}
     * in the adapter initialization code to set header data.
     *
     * @return header data
     */
    public H getHeader() {
        return header;
    }

    /**
     * Get item data in this adapter with the specified postion,
     * you should previously use {@link #setHeader(H header)}
     * in the adapter initialization code to set header data.
     *
     * @return item data in the specified postion
     */
    public final T getItem(int position) {
        if (hasHeader() && hasItems()) {
            --position;
        }
        return getRealItem(position);
    }

    protected abstract T getRealItem(int position);

    /**
     * Get footer data in this adapter, you should previously use {@link #setFooter(F footer)}
     * in the adapter initialization code to set footer data.
     *
     * @return footer data
     */
    public F getFooter() {
        return footer;
    }

    /**
     * If you need a header, you should set header data in the adapter initialization code.
     *
     * @param header header data
     */
    public void setHeader(H header) {
        this.header = header;
    }

    /**
     * If you need a footer, you should set footer data in the adapter initialization code.
     */
    public void setFooter(F footer) {
        this.footer = footer;
    }

    /**
     * Call this method to show hiding footer.
     */
    public void showFooter() {
        this.showFooter = true;
        notifyDataSetChanged();
    }

    /**
     * Call this method to hide footer.
     */
    public void hideFooter() {
        this.showFooter = false;
        notifyDataSetChanged();
    }

    /**
     * Returns true if the position type parameter passed as argument is equals to 0 and the adapter
     * has a not null header already configured.
     */
    public boolean isHeaderPosition(int position) {
        return hasHeader() && position == 0;
    }

    /**
     * Returns true if the position type parameter passed as argument is equals to
     * <code>getItemCount() - 1</code>
     * and the adapter has a not null header already configured.
     */
    public boolean isFooterPosition(int position) {
        int lastPosition = getItemCount() - 1;
        return hasFooter() && position == lastPosition;
    }

    protected int getFooterPosition() {
        return getItemCount() - 1;
    }

    protected int getHeaderPosition() {
        return 0;
    }

    /**
     * Returns true if the view type parameter passed as argument is equals to TYPE_HEADER.
     */
    protected boolean isHeaderType(int viewType) {
        return viewType == TYPE_HEADER;
    }

    /**
     * Returns true if the view type parameter passed as argument is equals to TYPE_FOOTER.
     */
    protected boolean isFooterType(int viewType) {
        return viewType == TYPE_FOOTER;
    }

    /**
     * Returns true if the header configured is not null.
     */
    public boolean hasHeader() {
        return showHeader && getHeader() != null;
    }

    public void showHeader() {
        this.showHeader = true;
        notifyDataSetChanged();
    }

    public void hideHeader() {
        this.showHeader = false;
        notifyDataSetChanged();
    }

    public int getRealPosition(int position ){

        if(hasHeader()) position --;
        return position;
    }

    /**
     * Returns true if the footer configured is not null.
     */
    public boolean hasFooter() {
        return showFooter && getFooter() != null;
    }

    /**
     * Returns true if the item configured is not empty.
     */
    private boolean hasItems() {
        return getRealItemCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder, Object t);
    }

    public interface OnFooterItemClickListener {
        void onFooterItemClick(RecyclerView.ViewHolder viewHolder, Object t);
    }

    public interface OnHeaderItemClickListener {
        void onHeaderItemClick(RecyclerView.ViewHolder viewHolder, Object t);
    }

    protected int getAdapterItemPosition(int realPosition) {
        return hasHeader() ? realPosition + 1 : realPosition;
    }

}