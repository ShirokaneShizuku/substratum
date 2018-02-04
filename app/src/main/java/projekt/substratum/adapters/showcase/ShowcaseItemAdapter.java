/*
 * Copyright (c) 2016-2017 Projekt Substratum
 * This file is part of Substratum.
 *
 * Substratum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Substratum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Substratum.  If not, see <http://www.gnu.org/licenses/>.
 */

package projekt.substratum.adapters.showcase;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

import projekt.substratum.R;
import projekt.substratum.common.Packages;
import projekt.substratum.common.References;
import projekt.substratum.databinding.ShowcaseEntryCardBinding;
import projekt.substratum.util.views.Lunchbar;

import static projekt.substratum.common.References.FADE_FROM_GRAYSCALE_TO_COLOR_DURATION;

public class ShowcaseItemAdapter extends RecyclerView.Adapter<ShowcaseItemAdapter.ViewHolder> {
    private List<ShowcaseItem> information;

    public ShowcaseItemAdapter(List<ShowcaseItem> information) {
        super();
        this.information = information;
    }

    @Override
    public ShowcaseItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                             int i) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.showcase_entry_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,
                                 int pos) {
        ShowcaseItem showcaseItem = this.information.get(pos);
        Context context = showcaseItem.getContext();
        ShowcaseEntryCardBinding viewBinding = viewHolder.getBinding();
        viewBinding.setShowcaseItem(showcaseItem);

        showcaseItem.setPaid(showcaseItem.getThemePricing().toLowerCase(Locale.US).equals(References.paidTheme));
        showcaseItem.setInstalled(Packages.isPackageInstalled(context, showcaseItem.getThemePackage()));

        viewBinding.themeCard.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(showcaseItem.getThemeLink()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Lunchbar.make(view,
                        context.getString(R.string.activity_missing_toast),
                        Snackbar.LENGTH_LONG).show();
            }
        });

        // Prettify the UI with fading desaturating colors!
        ColorMatrix matrix = new ColorMatrix();
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(FADE_FROM_GRAYSCALE_TO_COLOR_DURATION);
        animation.addUpdateListener(animation1 -> {
            matrix.setSaturation(animation1.getAnimatedFraction());
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            viewBinding.backgroundImage.setColorFilter(filter);
        });
        animation.start();
    }

    @Override
    public int getItemCount() {
        return this.information.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ShowcaseEntryCardBinding binding;

        ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        ShowcaseEntryCardBinding getBinding() {
            return binding;
        }
    }
}