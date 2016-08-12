package joppi.pier.parkingfinder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class AppUtils
{
	public static int generateColorFromRank(int startColor, int endColor, double rank)
	{
		// START colors
		int sA = Color.alpha(startColor);
		int sR = Color.red(startColor);
		int sG = Color.green(startColor);
		int sB = Color.blue(startColor);

		// END colors
		int eA = Color.alpha(endColor);
		int eR = Color.red(endColor);
		int eG = Color.green(endColor);
		int eB = Color.blue(endColor);

		// DELTAs
		int dA = eA - sA;
		int dR = eR - sR;
		int dG = eG - sG;
		int dB = eB - sB;

		// FINAL colors
		int fA = (int)((dA/1.0*rank)+sA);
		int fR = (int)((dR/1.0*rank)+sR);
		int fG = (int)((dG/1.0*rank)+sG);
		int fB = (int)((dB/1.0*rank)+sB);

		if(sA == 0 && eA == 0)
			fA = 0xFF;

		return Color.argb(fA, fR , fG, fB);
	}

	public static int generateColorFromRank(int startColor, int midColor, int endColor, double rank)
	{
		// START colors
		int sA = Color.alpha(startColor);
		int sR = Color.red(startColor);
		int sG = Color.green(startColor);
		int sB = Color.blue(startColor);

		// MIDDLE colors
		int mA = Color.alpha(midColor);
		int mR = Color.red(midColor);
		int mG = Color.green(midColor);
		int mB = Color.blue(midColor);

		// END colors
		int eA = Color.alpha(endColor);
		int eR = Color.red(endColor);
		int eG = Color.green(endColor);
		int eB = Color.blue(endColor);

		// FINAL colors using rational Bézier quadratic curve!
		// 3x weight on mid color for better approx.
		int fA = (int) getRationalBezierQuadraticValue(rank, sA, 1.0, mA, 3.0, eA, 1.0);
		int fR = (int) getRationalBezierQuadraticValue(rank, sR, 1.0, mR, 3.0, eR, 1.0);
		int fG = (int) getRationalBezierQuadraticValue(rank, sG, 1.0, mG, 3.0, eG, 1.0);
		int fB = (int) getRationalBezierQuadraticValue(rank, sB, 1.0, mB, 3.0, eB, 1.0);

		// Set default alpha if omitted
		if(sA == 0 && eA == 0)
			fA = 0xFF;

		return Color.argb(fA, fR , fG, fB);
	}

	public static double getRationalBezierQuadraticValue(double value, double startPt, double w0, double ctrlPt, double w1, double endPt, double w2)
	{
		double num = ((1 - value) * (1 - value) * startPt * w0) + (2 * (1 - value) * value * ctrlPt * w1) + (value * value * endPt * w2);
		double den = ((1 - value) * (1 - value) * w0) + (2 * (1 - value) * value * w1) + (value * value * w2);
		return num/den;
	}

	public static BitmapDescriptor getCustomParkingMarker(double rank)
	{
		Drawable marker = ParkingFinderApplication.getAppContext().getResources().getDrawable(R.drawable.marker_parking_road);
		Drawable markerBg = ParkingFinderApplication.getAppContext().getResources().getDrawable(R.drawable.marker_background);
		if(marker != null && markerBg != null)
		{
			// From GREEN (0x30e0c0) to YELLOW@0.5 (0xffc280) to RED (0xff7080)
			int color = generateColorFromRank(0x30e0c0, 0xffc280, 0xff7080, rank);
			marker.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			Canvas canvas = new Canvas();
			Bitmap bitmap = Bitmap.createBitmap(marker.getIntrinsicWidth(), marker.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			canvas.setBitmap(bitmap);
			markerBg.setBounds(0, 0, markerBg.getIntrinsicWidth(), markerBg.getIntrinsicHeight());
			markerBg.draw(canvas);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
			marker.draw(canvas);
			marker.setColorFilter(null);
			return BitmapDescriptorFactory.fromBitmap(bitmap);
		}
		return null;
	}
}
