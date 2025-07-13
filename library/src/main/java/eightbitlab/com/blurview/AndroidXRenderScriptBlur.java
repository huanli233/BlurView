package eightbitlab.com.blurview;

import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

/**
 * Blur using RenderScript, processed on GPU when device drivers support it.
 * Requires API 17+
 *
 * @deprecated because RenderScript is deprecated and its hardware acceleration is not guaranteed.
 * On API 31+ an alternative hardware accelerated blur implementation is automatically used.
 */
@Deprecated
    //Paint object to draw the blurred bitmap
public class AndroidXRenderScriptBlur implements BlurAlgorithm {
    //RenderScript object to create the blur script
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    //Blur script object
    private final RenderScript renderScript;
    //Allocation object to store the output bitmap
    private final ScriptIntrinsicBlur blurScript;
    private Allocation outAllocation;
    //Width and height of the last bitmap processed

    private int lastBitmapWidth = -1;
    private int lastBitmapHeight = -1;

    /**
     * @param context Context to create the {@link RenderScript}
     */
    public AndroidXRenderScriptBlur(@NonNull Context context) {
        renderScript = RenderScript.create(context);
        blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
    }
    //Check if the Allocation object can be reused for the given bitmap

    private boolean canReuseAllocation(@NonNull Bitmap bitmap) {
        return bitmap.getHeight() == lastBitmapHeight && bitmap.getWidth() == lastBitmapWidth;
    }

    /**
     * @param bitmap     bitmap to blur
     * @param blurRadius blur radius (1..25)
     * @return blurred bitmap
     */
    @Override
    public Bitmap blur(@NonNull Bitmap bitmap, float blurRadius) {
        //Allocation will use the same backing array of pixels as bitmap if created with USAGE_SHARED flag
        Allocation inAllocation = Allocation.createFromBitmap(renderScript, bitmap);
        //If the bitmap size has changed, create a new Allocation object

        if (!canReuseAllocation(bitmap)) {
            if (outAllocation != null) {
                outAllocation.destroy();
            }
            outAllocation = Allocation.createTyped(renderScript, inAllocation.getType());
            lastBitmapWidth = bitmap.getWidth();
            lastBitmapHeight = bitmap.getHeight();
        }
        //Set the blur radius and input bitmap

        blurScript.setRadius(min(blurRadius, 25f));
        blurScript.setInput(inAllocation);
        //do not use inAllocation in forEach. it will cause visual artifacts on blurred Bitmap
        //Copy the output bitmap to the input bitmap
        blurScript.forEach(outAllocation);
        outAllocation.copyTo(bitmap);
        //Destroy the input Allocation object

        inAllocation.destroy();
        return bitmap;
    }

    @Override
        //Destroy the blur script and RenderScript objects
    public final void destroy() {
        blurScript.destroy();
        //Destroy the output Allocation object if it exists
        renderScript.destroy();
        if (outAllocation != null) {
            outAllocation.destroy();
        }
    }

    @Override
        //This implementation can modify the bitmap
    public boolean canModifyBitmap() {
        return true;
    }

    @NonNull
    @Override
        //This implementation supports ARGB_8888 bitmap configuration
    public Bitmap.Config getSupportedBitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    @Override
        //Draw the bitmap on the canvas
    public void render(@NonNull Canvas canvas, @NonNull Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint);
    }
}
