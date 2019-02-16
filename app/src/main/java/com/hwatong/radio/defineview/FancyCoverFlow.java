package com.hwatong.radio.defineview;
import com.hwatong.radio.adapter.FancyCoverFlowAdapter;
import com.hwatong.radio.ui.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

@SuppressWarnings("deprecation")
public class FancyCoverFlow extends Gallery {

	/**max int*/
	public static final float SCALEDOWN_GRAVITY_TOP = 0.0f;
	public static final float SCALEDOWN_GRAVITY_CENTER = 0.5f;
	public static final float SCALEDOWN_GRAVITY_BOTTOM = 1.0f;
	public static final int ACTION_DISTANCE_AUTO = Integer.MAX_VALUE;

	private int actionDistance;
	private int maxRotation = 75;
	private int reflectionGap = 20;
	
	private float unselectedAlpha;
	private float unselectedScale;
	private float unselectedSaturation;
	private float reflectionRatio = 0.4f;
	private float scaleDownGravity = SCALEDOWN_GRAVITY_CENTER;
	
	private boolean reflectionEnabled = false;

	private Camera transformationCamera;

	public FancyCoverFlow(Context context) {
		
		this(context,null);
	}

	public FancyCoverFlow(Context context, AttributeSet attrs) {
		
		this(context, attrs,-1);
	}

	public FancyCoverFlow(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		
		this.initialize();
		this.applyXmlAttributes(attrs);
		
		/**允许转换 **/
        this.setStaticTransformationsEnabled(true);  
        /**允许设置子view的顺序**/  
        this.setChildrenDrawingOrderEnabled(true);  
        
	}

	private void initialize() {
		
		this.setSpacing(-20);
		this.setUnselectedSaturation(1);
		this.transformationCamera = new Camera();
	}

	private void applyXmlAttributes(AttributeSet attrs) {
		
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.FancyCoverFlow);

		this.actionDistance = a
				.getInteger(R.styleable.FancyCoverFlow_actionDistance,
						ACTION_DISTANCE_AUTO);
		this.scaleDownGravity = a.getFloat(
				R.styleable.FancyCoverFlow_scaleDownGravity, 1.0f);
		this.maxRotation = a.getInteger(R.styleable.FancyCoverFlow_maxRotation,
				45);
		this.unselectedAlpha = a.getFloat(
				R.styleable.FancyCoverFlow_unselectedAlpha, 0.3f);
		this.unselectedSaturation = a.getFloat(
				R.styleable.FancyCoverFlow_unselectedSaturation, 0.0f);
		this.unselectedScale = a.getFloat(
				R.styleable.FancyCoverFlow_unselectedScale, 0.75f);
		a.recycle();
	}

	public float getReflectionRatio() {
		
		return reflectionRatio;
	}

	public void setReflectionRatio(float reflectionRatio) {
		
		if (reflectionRatio <= 0 || reflectionRatio > 0.5f) {
			
			throw new IllegalArgumentException(
					"reflectionRatio may only be in the interval (0, 0.5]");
		}

		this.reflectionRatio = reflectionRatio;

		if (this.getAdapter() != null) {
			
			((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
		}
	}

	public int getReflectionGap() {
		
		return reflectionGap;
	}

	public void setReflectionGap(int reflectionGap) {
		
		this.reflectionGap = reflectionGap;

		if (this.getAdapter() != null) {
			
			((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
		}
	}

	public boolean isReflectionEnabled() {
		
		return reflectionEnabled;
	}

	public void setReflectionEnabled(boolean reflectionEnabled) {
		
		this.reflectionEnabled = reflectionEnabled;

		if (this.getAdapter() != null) {
			
			((FancyCoverFlowAdapter) this.getAdapter()).notifyDataSetChanged();
		}
	}
    

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		if (!(adapter instanceof FancyCoverFlowAdapter)) {
			
			throw new ClassCastException(FancyCoverFlow.class.getSimpleName()
					+ " only works in conjunction with a "
					+ FancyCoverFlowAdapter.class.getSimpleName());
		}
		super.setAdapter(adapter);
	}

	public int getMaxRotation() {
		
		return maxRotation;
	}
	
	public void setMaxRotation(int maxRotation) {
		
		this.maxRotation = maxRotation;
	}
	
	public float getUnselectedAlpha() {
		
		return this.unselectedAlpha;
	}

	public float getUnselectedScale() {
		
		return unselectedScale;
	}

	public void setUnselectedScale(float unselectedScale) {
		
		this.unselectedScale = unselectedScale;
	}

	public float getScaleDownGravity() {
		
		return scaleDownGravity;
	}

	public void setScaleDownGravity(float scaleDownGravity) {
		
		this.scaleDownGravity = scaleDownGravity;
	}

	public int getActionDistance() {
		
		return actionDistance;
	}

	public void setActionDistance(int actionDistance) {
		
		this.actionDistance = actionDistance;
	}

	@Override
	public void setUnselectedAlpha(float unselectedAlpha) {
		
		super.setUnselectedAlpha(unselectedAlpha);
		this.unselectedAlpha = unselectedAlpha;
	}

	public float getUnselectedSaturation() {
		
		return unselectedSaturation;
	}

	public void setUnselectedSaturation(float unselectedSaturation) {
		
		this.unselectedSaturation = unselectedSaturation;
	}
    
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		
		FancyCoverFlowItemWrapper item = (FancyCoverFlowItemWrapper) child;

		final int coverFlowWidth = this.getWidth();
		final int coverFlowCenter = coverFlowWidth / 2;
		final int childWidth = item.getWidth();
		final int childHeight = item.getHeight();
		final int childCenter = item.getLeft() + childWidth / 2;
        
		boolean autoDistance = (this.actionDistance == ACTION_DISTANCE_AUTO);
		final int actionDistance = autoDistance ? (int) ((coverFlowWidth + childWidth) / 2.0f)
				                                : this.actionDistance;
        
		float maxDistance = Math.max(-1.0f, (1.0f / actionDistance) * (childCenter - coverFlowCenter));
		final float effectsAmount = Math.min(1.0f,maxDistance);

		t.clear();
		t.setTransformationType(Transformation.TYPE_BOTH);

		/**透明度**/
		if (this.unselectedAlpha != 1) {
			
			final float alphaAmount = (this.unselectedAlpha - 1) * Math.abs(effectsAmount) + 1;
			t.setAlpha(alphaAmount);
		}
		
		/**饱和度**/
		if (this.unselectedSaturation != 1) {
			
			final float saturationAmount = (this.unselectedSaturation - 1) * Math.abs(effectsAmount) + 1;
			item.setSaturation(saturationAmount);
		}

		final Matrix imageMatrix = t.getMatrix();

		/**旋转**/
		if (this.maxRotation != 0) {
			
			final int rotationAngle = (int) (effectsAmount * this.maxRotation);
			this.transformationCamera.save();
			this.transformationCamera.rotateY(rotationAngle);
			this.transformationCamera.getMatrix(imageMatrix);
			this.transformationCamera.restore();
		}

		/**缩放**/
		if (this.unselectedScale != 1) {
			
			final float zoomAmount = (this.unselectedScale - 1) * Math.abs(effectsAmount) + 1;
			final float translateX = childWidth / 2.0f;
			final float translateY = childHeight * this.scaleDownGravity;
			imageMatrix.preTranslate(-translateX, -translateY);
			imageMatrix.postScale(zoomAmount, zoomAmount);
			imageMatrix.postTranslate(translateX, translateY);
		}

		return true;
	}
	
	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		
		/**当前选择的子view的索引**/
        /**int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();**/
        
		View child = getChildAt(i);
		
		if (i == 1 || i == 5 || i == 0) {
			
			child.setAlpha(0.15f);
		}
		
		if (i == 3) {
			
			child.setAlpha(1.0f);
		}
		
		
		if (i == 2 || i == 4) {
			
			child.setAlpha(0.45f);
		}
		
        return i;
	}
    
	/***
	 * 重写滑动方法  减缓滑动速度
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		return false;
	}

}
