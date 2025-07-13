package eightbitlab.com.blurview;

import static eightbitlab.com.blurview.BlurController.DEFAULT_SCALE_FACTOR;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;

/**
 * A fallback "blur" algorithm for older APIs (below 17).
 * It doesn't actually blur the bitmap, but instead draws a translucent overlay
 * to simulate a simple glass effect. The translucency is determined by the blur radius.
 */
public class LegacyBlurAlgorithm implements BlurAlgorithm {
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private int overlayColor;
    private float radius;

    public LegacyBlurAlgorithm(@NonNull int overlayColor) {
        this.overlayColor = overlayColor;
    }

    @Override
    public Bitmap blur(Bitmap bitmap, float blurRadius) {
        // We don't perform any blurring. Instead, we'll draw the overlay in the apply method.
        // The radius is saved to calculate alpha.
        this.radius = blurRadius;
        return bitmap;
    }

    @Override
    public void destroy() {
        // No native resources to destroy
    }

    @Override
    public boolean canModifyBitmap() {
        return true;
    }

    @Override
    public Bitmap.Config getSupportedBitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    public float getScaleFactor() {
        return DEFAULT_SCALE_FACTOR;
    }

    @Override
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
        // Logic to calculate alpha.
        // Map radius (0-25f) to alpha (0-178, which is 70% of 255).
        // The scaling factor is (255 * 0.7) / 25 = 178.5 / 25 = 7.14
        float scalingFactor = 9f;

        // Clamp the radius to ensure it's within the 0-25f range before calculation.
        float clampedRadius = Math.max(0f, Math.min(25f, radius));
        int alpha = (int) (clampedRadius * scalingFactor);

        // Combine the original overlayColor's RGB with the new alpha.
        int color = (overlayColor & 0x00FFFFFF) | (alpha << 24);
        paint.setColor(color);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }
    
    /**
     * Sets the overlay color to be drawn on top of the blurred content.
     * @param overlayColor The color to use for the overlay.
     */
    public void setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
    }
}