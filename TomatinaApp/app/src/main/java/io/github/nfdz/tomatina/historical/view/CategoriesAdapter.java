package io.github.nfdz.tomatina.historical.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.github.nfdz.tomatina.R;
import io.github.nfdz.tomatina.TomatinaApp;
import timber.log.Timber;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    public interface Callback {
        void onCategoryClick(String category);
    }

    private final LayoutInflater layoutInflater;
    private final int selectedTextColor;
    private final int unselectedTextColor;
    private final int horizontalMargin;
    private final Callback callback;

    private List<String> categories;
    private Set<String> selectedCategories;

    public CategoriesAdapter(Context context, Callback callback) {
        this.callback = callback;
        this.layoutInflater = LayoutInflater.from(context);
        this.selectedTextColor = ContextCompat.getColor(context, R.color.textColorDark);
        this.unselectedTextColor = ContextCompat.getColor(context, R.color.textColorLight);
        this.horizontalMargin = context.getResources().getDimensionPixelSize(R.dimen.historical_category_margin_horizontal);
    }

    public void setCategories(Set<String> categories) {
        this.categories = new ArrayList<>(categories);
        Collections.sort(this.categories);
        notifyDataSetChanged();
    }

    public void setSelectedCategories(Set<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        try {
            String category = categories.get(position);
            boolean isSelected = selectedCategories != null && selectedCategories.contains(category);
            boolean isFirstOne = position == 0;
            boolean isLastOne = position == (categories.size() - 1);
            holder.bindCategory(category, isSelected, isFirstOne, isLastOne);
        } catch (Exception e) {
            Timber.e(e, "Cannot bind category view holder");
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView item_tv_category;

        CategoryViewHolder(View itemView) {
            super(itemView);
            this.item_tv_category = itemView.findViewById(R.id.item_tv_category);
            item_tv_category.setOnClickListener(this);
        }

        public void bindCategory(String category, boolean isSelected, boolean isFirstOne, boolean isLastOne) {
            itemView.setPadding(isFirstOne ? horizontalMargin * 2 : horizontalMargin,
                    0,
                    isLastOne ? horizontalMargin * 2 : horizontalMargin,
                    0);
            item_tv_category.setText(category);
            if (isSelected)  {
                item_tv_category.setTextColor(selectedTextColor);
                item_tv_category.setBackgroundResource(R.drawable.shape_category_selected);
            } else {
                item_tv_category.setTextColor(unselectedTextColor);
                item_tv_category.setBackgroundResource(R.drawable.shape_category);
            }
        }

        @Override
        public void onClick(View v) {
            try {
                String category = categories.get(getAdapterPosition());
                callback.onCategoryClick(category);
            } catch (Exception e) {
                Timber.e(e, "Cannot handle category view holder click");
            }
        }

    }

}
