package com.tvmining.wifiplus.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

public class LayoutCalculator {
	public static final int BLACKCIRCLE_RADIUS = 13;
	private static final int FOLDER_GAP = 3;
	private static final int FOLDER_ICON_SIZE = 13;
	public static final int ICON_HEIGHT = 60;
	public static final int ICON_WIDTH = 60;
	public static final int IPHONE_ICON_HEIGHT = 87;
	public static final int IPHONE_ICON_WIDTH = 86;
	public static final int PAGER_GAP = 10;
	public static final int PAGER_HEIGHT = 8;
	public static final int REDCIRCLE_MARGIN_TOP = 6;
	public static final int REDCIRCLE_RADIUS = 12;
	private static final float TABLET_SIZE = 4.67F;
	public static final int WALLPAPER_SHADOW_ADJUSTMENT = 16;
	private static final float WIDE_SIZE = 3.8F;
	public static int columns = 4;
	public static int dockColumns = 4;
	public static int iconsPerPage;
	public static int rows = 4;
	public int bcMarginLeft;
	public int bcMarginTop;
	public int cMarginTop;
	public boolean canBeTablet = false;
	public int dockBarHeight;
	public int folderGap;
	public int folderIconSize;
	public int folderMargin;
	public int folderMaxHeight;
	public int fullMarginLeft;
	public int fullMarginLeftBlackCircle;
	public int fullMarginRight;
	public int gapFolderV;
	public int gapH;
	public int gapV;
	public int height;
	public int iconHeight;
	public int iconLastMarginRight;
	public int iconMarginLeft;
	public int iconMarginLeftBlackCircle;
	public int iconWidth;
	public boolean isHighResolution;
	public boolean isPortrait = true;
	public boolean isTablet = false;
	public boolean isWide = false;
	public int itemHeight;
	public int marginLeft;
	public int marginTop;
	public int pageHeight;
	public int pagerGap;
	public int rcMarginRight;
	public int rcMarginTop;
	private float scale;
	public int searchIconHeight;
	public int searchIconWidth;
	public int shadowMarginBottom;
	public int shadowMarginLeft;
	public int shadowMarginRight;
	public int textTop;
	public int width;

	public LayoutCalculator(Context context, boolean enableStretch, boolean tabletLayout) {
		canBeTablet = false;
		isTablet = false;
		isWide = false;
		isPortrait = true;
		DisplayMetrics displaymetrics = context.getResources()
				.getDisplayMetrics();
		scale = displaymetrics.density;
		int dpi = displaymetrics.densityDpi;
		Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();

		float f;

		int k;
		int l;

		if (display.getWidth() > display.getHeight())
			isPortrait = true;
		else
			isPortrait = false;

		f = (float) Math.sqrt(displaymetrics.widthPixels
				* displaymetrics.widthPixels + displaymetrics.heightPixels
				* displaymetrics.heightPixels)
				/ (float) dpi;
		if (f >= TABLET_SIZE)
			canBeTablet = true;
		else
			canBeTablet = false;

		if (canBeTablet && tabletLayout)
			isTablet = true;
		else
			isTablet = false;

		if (f >= WIDE_SIZE)
			isWide = true;
		else
			isWide = false;

		/*if (enableStretch) {
			if (isTablet) {
				int k1 = (int) (0.545F * (float) dpi * (f / 9.697F));
				iconHeight = k1;
				iconWidth = k1;
			} else if (isWide) {
				int j1 = (int) (0.33F * (float) dpi * (f / 3.496F));
				iconHeight = j1;
				iconWidth = j1;
			} else {
				iconWidth = dpToPixel(ICON_WIDTH);
				iconHeight = dpToPixel(ICON_HEIGHT);
			}
		} 
		else {
			iconWidth = dpToPixel(ICON_WIDTH);
			iconHeight = dpToPixel(ICON_HEIGHT);
		}*/
		
		iconWidth = IPHONE_ICON_WIDTH;
		iconHeight = IPHONE_ICON_HEIGHT;
		
		if (isTablet) {
			int i1 = dpToPixel(50);
			searchIconHeight = i1;
			searchIconWidth = i1;
			textTop = iconHeight + Math.min(dpToPixel(16), propIconHeight(16));
			itemHeight = iconHeight
					+ Math.min(dpToPixel(19), propIconHeight(19));
			dockBarHeight = itemHeight
					+ Math.max(dpToPixel(6), propIconHeight(6));
		} 
		else {
			int j = dpToPixel(29);
			searchIconHeight = j;
			searchIconWidth = j;
			textTop = iconHeight + Math.min(dpToPixel(13), propIconHeight(13));
			itemHeight = iconHeight + Math.min(dpToPixel(WALLPAPER_SHADOW_ADJUSTMENT), propIconHeight(WALLPAPER_SHADOW_ADJUSTMENT));
			dockBarHeight = itemHeight + Math.max(dpToPixel(4), propIconHeight(4));
		}
		k = dpToPixel(13);
		l = dpToPixel(1);
		bcMarginLeft = k - l;
		bcMarginTop = k - l;
		rcMarginTop = dpToPixel(6);
		rcMarginRight = rcMarginTop;
		cMarginTop = Math.max(bcMarginTop, rcMarginTop);
		pagerGap = dpToPixel(PAGER_GAP);
		folderIconSize = (FOLDER_ICON_SIZE * iconWidth) / 60;
		folderGap = (FOLDER_GAP * iconWidth) / 60;
		folderMaxHeight = FOLDER_GAP * folderIconSize + 2 * folderGap;
		folderMargin = (iconHeight - folderMaxHeight) / 2;
	}

	public float dpToPixel(float paramFloat) {
		return paramFloat * this.scale;
	}

	public float pixelToDp(float paramInt){
		return paramInt / this.scale;
	}
	
	public int dpToPixel(int paramInt) {
		return (int) (0.5F + paramInt * this.scale);
	}

	public int getBlackCircleMarginLeft() {
		return this.bcMarginLeft;
	}

	public int getBlackCircleMarginTop() {
		return this.bcMarginTop;
	}

	public int getFullItemHeight() {
		return this.rcMarginTop + this.itemHeight + this.shadowMarginBottom;
	}

	public int getFullItemWidth() {
		return this.fullMarginLeft + this.iconWidth
				+ Math.max(this.iconLastMarginRight, this.shadowMarginRight);
	}

	public int getFullItemWidthBlackCircle() {
		return this.fullMarginLeftBlackCircle + this.iconWidth
				+ this.fullMarginRight;
	}

	public int getHorizontalGap() {
		return this.gapH;
	}

	public int getHorizontalGap(int paramInt) {
		return (this.width - paramInt * this.iconWidth) / (paramInt + 1);
	}

	public int[] getHorizontalGap2(int paramInt) {
		int i = this.width - paramInt * this.iconWidth;
		int j = i / (paramInt + 1);
		int k = i % (paramInt + 1) / 2;
		int[] arrayOfInt = new int[2];
		arrayOfInt[0] = k;
		arrayOfInt[1] = j;
		return arrayOfInt;
	}

	public int getIconHeight() {
		return this.iconHeight;
	}

	public Point getIconLocation(int paramInt) {
		int i = paramInt / columns;
		int j = paramInt % columns;
		Point localPoint = new Point();
		localPoint.x = (this.marginLeft + this.gapH + j
				* (this.iconWidth + this.gapH));
		localPoint.y = (this.marginTop + this.gapV + i
				* (this.itemHeight + this.gapV));
		return localPoint;
	}

	public int getIconMarginLeft() {
		return this.iconMarginLeft;
	}

	public int getIconWidth() {
		return this.iconWidth;
	}

	public int getItemHeight() {
		return this.itemHeight;
	}

	public int getItemWidth() {
		return this.iconMarginLeft + this.iconWidth + this.iconLastMarginRight;
	}

	public int getItemWidthBlackCircle() {
		return this.iconMarginLeftBlackCircle + this.iconWidth
				+ this.iconLastMarginRight;
	}

	public int getMarginLeft() {
		return this.marginLeft;
	}

	public int getMarginTop() {
		return this.marginTop;
	}

	public int getPageHeight() {
		return this.pageHeight;
	}

	public int getRedCircleMarginRight() {
		return this.rcMarginRight;
	}

	public int getTextTop() {
		return this.textTop;
	}

	public int getVerticalGap() {
		return this.gapV;
	}

	public int getVitalHeight() {
		return this.rcMarginTop + this.itemHeight * rows + this.gapV
				* (-1 + rows) + this.shadowMarginBottom;
	}

	public int getVitalLeft() {
		return this.marginLeft + this.gapH - this.fullMarginLeft;
	}

	public int getVitalTop() {
		return this.marginTop + this.gapV - this.rcMarginTop;
	}

	public int getVitalWidth() {
		return this.fullMarginLeft
				+ this.iconWidth
				* columns
				+ this.gapH
				* (-1 + columns)
				+ Math.max(
						Math.max(this.shadowMarginRight, this.rcMarginRight),
						this.iconMarginLeft);
	}

	public int getWidth() {
		return this.width;
	}

	
	public float propIconHeight(float paramFloat) {
		return paramFloat * this.iconHeight / ICON_HEIGHT;
	}

	public int propIconHeight(int paramInt) {
		return paramInt * this.iconHeight / ICON_HEIGHT;
	}

	public float propIconWidth(float paramFloat) {
		return paramFloat * this.iconWidth / ICON_WIDTH;
	}

	public int propIconWidth(int paramInt) {
		return paramInt * this.iconWidth / ICON_WIDTH;
	}

	public int reflect(Canvas paramCanvas, int paramInt1, int paramInt2) {
		int i = paramInt2 + 2 * (this.iconHeight - dpToPixel(3));
		paramCanvas.clipRect(new Rect(paramInt1, paramInt2 + this.iconHeight
				- dpToPixel(2), paramInt1 + this.iconWidth, i));
		paramCanvas.scale(1.0F, -1.0F, 0.0F, i);
		return i;
	}

	public Rect toItemRect(int paramInt1, int paramInt2) {
		Rect localRect = new Rect();
		localRect.left = (paramInt1 - this.iconMarginLeft);
		localRect.right = (paramInt1 + this.iconWidth + this.iconMarginLeft + this.rcMarginRight);
		localRect.top = (paramInt2 - this.rcMarginTop);
		localRect.bottom = (paramInt2 + this.itemHeight);
		return localRect;
	}
}
