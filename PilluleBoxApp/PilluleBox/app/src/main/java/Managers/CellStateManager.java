package Managers;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.pillulebox.R;

public class CellStateManager {
    public static final int STATE_EMPTY = 0;
    public static final int STATE_AVAILABLE = 1;
    public static final int STATE_SINGLE_MODE = 2;
    public static final int STATE_SEQUENTIAL_MODE = 3;
    public static final int STATE_BASIC_MODE = 4;

    public static final int SECONDARY_STATE_NONE = 0;
    public static final int SECONDARY_STATE_REQUIRED = 1;
    public static final int SECONDARY_STATE_WARNING = 2;

    public static void updateCellState(View cellView, int primaryState, int secondaryState, Context context) {
        GradientDrawable shape = (GradientDrawable) cellView.getBackground();

        int backgroundColor = getPrimaryStateColor(primaryState, context);
        shape.setColor(backgroundColor);

        int borderColor = getSecondaryStateColor(secondaryState, context);
        shape.setStroke(4, borderColor);
    }

    private static int getPrimaryStateColor(int state, Context context) {
        switch (state) {
            case STATE_AVAILABLE:
                return ContextCompat.getColor(context, R.color.cell_available);
            case STATE_SINGLE_MODE:
                return ContextCompat.getColor(context, R.color.cell_single_mode);
            case STATE_SEQUENTIAL_MODE:
                return ContextCompat.getColor(context, R.color.cell_sequential_mode);
            case STATE_BASIC_MODE:
                return ContextCompat.getColor(context, R.color.cell_basic_mode);
            case STATE_EMPTY:
            default:
                return ContextCompat.getColor(context, R.color.cell_empty);
        }
    }

    private static int getSecondaryStateColor(int state, Context context) {
        switch (state) {
            case SECONDARY_STATE_REQUIRED:
                return ContextCompat.getColor(context, R.color.cell_required);
            case SECONDARY_STATE_WARNING:
                return ContextCompat.getColor(context, R.color.cell_warning);
            case SECONDARY_STATE_NONE:
            default:
                return ContextCompat.getColor(context, R.color.black);
        }
    }
}
