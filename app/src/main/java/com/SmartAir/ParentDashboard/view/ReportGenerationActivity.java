package com.SmartAir.ParentDashboard.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportGenerationActivity {

    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    public static final String FILE_PROVIDER_AUTHORITY = "com.smartair.fileprovider";

    // --- Data Models ---
    public static class AsthmaReportData {
        public String childName;
        public String currentZone;
        public int adherenceScore;
        public String period;
        public List<DailyLog> logs;

        public List<Integer> zoneData;

        public AsthmaReportData(String childName, String currentZone, int adherenceScore, String period, List<DailyLog> logs, List<Integer> zoneData ){
            this.childName = childName;
            this.currentZone = currentZone;
            this.adherenceScore = adherenceScore;
            this.period = period;
            this.logs = logs;
            this.zoneData = zoneData;
        }
    }

    public static class DailyLog {
        public String date;
        public List<String> triggers; // Corresponds to 'Triggers' field
        public String note;

        public DailyLog(String date, List<String> triggers, String note) {
            this.date = date;
            this.triggers = triggers;
            this.note = note;
        }
    }

    // --- PDF Generation Logic ---

    public static File generatePdfFromData(Context context, AsthmaReportData data) {
        // 1. Create the View based on Data
        View reportView = createRealReportView(context, data);

        // 2. Measure and Layout (Crucial for PDF generation without displaying on screen)
        reportView.measure(
                View.MeasureSpec.makeMeasureSpec(PAGE_WIDTH, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(PAGE_HEIGHT, View.MeasureSpec.UNSPECIFIED) // Allow height to grow
        );
        reportView.layout(0, 0, reportView.getMeasuredWidth(), reportView.getMeasuredHeight());

        // 3. Create Document
        PdfDocument document = new PdfDocument();
        // We use the measured height of the view, or standard A4 height, whichever is larger
        int height = Math.max(PAGE_HEIGHT, reportView.getMeasuredHeight());

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // 4. Draw
        reportView.draw(page.getCanvas());
        document.finishPage(page);

        // 5. Save
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "AsthmaReport_" + data.childName + "_" + timestamp + ".pdf";
        File cacheDir = new File(context.getCacheDir(), "Reports");
        if (!cacheDir.exists()) cacheDir.mkdirs();
        File outputFile = new File(cacheDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            document.writeTo(fos);
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            document.close();
        }
    }

    public static void sharePdfFile(Context context, File file) {
        if (file == null || !file.exists()) return;
        Uri contentUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Report"));
    }

    // --- View Construction ---

    private static View createRealReportView(Context context, AsthmaReportData data) {
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        mainLayout.setPadding(40, 40, 40, 40);

        // 1. Header
        TextView title = new TextView(context);
        title.setText("SmartAir Health Report");
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.addView(title);

        TextView subTitle = new TextView(context);
        subTitle.setText("Patient: " + data.childName + "  |  " + data.period);
        subTitle.setTextSize(14);
        subTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subTitle.setPadding(0, 0, 0, 20);
        mainLayout.addView(subTitle);

        // 2. Status Section (Zone + Adherence)
        LinearLayout statusRow = new LinearLayout(context);
        statusRow.setOrientation(LinearLayout.HORIZONTAL);
        statusRow.setWeightSum(2);
        statusRow.setPadding(0, 20, 0, 20);

        // Left: Zone
        LinearLayout zoneContainer = new LinearLayout(context);
        zoneContainer.setOrientation(LinearLayout.VERTICAL);
        zoneContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView zoneLabel = new TextView(context);
        zoneLabel.setText("Current Zone");
        zoneLabel.setTextSize(16);
        zoneContainer.addView(zoneLabel);

        TextView zoneValue = new TextView(context);
        zoneValue.setText(data.currentZone);
        zoneValue.setTextSize(22);
        zoneValue.setPadding(0, 10, 0, 0);

        // Colorize Zone
        if ("Green".equalsIgnoreCase(data.currentZone)) zoneValue.setTextColor(Color.parseColor("#4CAF50"));
        else if ("Yellow".equalsIgnoreCase(data.currentZone)) zoneValue.setTextColor(Color.parseColor("#FFC107"));
        else zoneValue.setTextColor(Color.parseColor("#F44336"));

        zoneContainer.addView(zoneValue);
        statusRow.addView(zoneContainer);

        // Right: Adherence Score
        LinearLayout scoreContainer = new LinearLayout(context);
        scoreContainer.setOrientation(LinearLayout.VERTICAL);
        scoreContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView scoreLabel = new TextView(context);
        scoreLabel.setText("Controller Adherence");
        scoreLabel.setTextSize(16);
        scoreContainer.addView(scoreLabel);

        TextView scoreValue = new TextView(context);
        scoreValue.setText(data.adherenceScore + "%");
        scoreValue.setTextSize(22);
        scoreValue.setPadding(0, 10, 0, 0);
        scoreValue.setTextColor(Color.DKGRAY);
        scoreContainer.addView(scoreValue);

        statusRow.addView(scoreContainer);
        mainLayout.addView(statusRow);

        // Divider
        View div = new View(context);
        div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        div.setBackgroundColor(Color.LTGRAY);
        mainLayout.addView(div);

        // 1. Chart Title
        TextView chartTitle = new TextView(context);
        chartTitle.setText("Zone Distribution (Last 3 Months)");
        chartTitle.setTextSize(18);
        chartTitle.setPadding(0, 30, 0, 10);
        chartTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        mainLayout.addView(chartTitle);

        // 2. The Pie Chart View
        // We must give it a fixed height so the layout knows how big to draw it
        if (data.zoneData != null && data.zoneData.size() >= 3) {
            ZonePieChartView pieChart = new ZonePieChartView(context, data.zoneData);
            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400 // Fixed height in pixels (adjust as needed)
            );
            chartParams.setMargins(0, 0, 0, 20); // Add bottom margin
            mainLayout.addView(pieChart, chartParams);

            // Optional: Simple Legend
            TextView legend = new TextView(context);
            legend.setText("Red: " + data.zoneData.get(0) + " | Yellow: " + data.zoneData.get(1) + " | Green: " + data.zoneData.get(2));
            legend.setGravity(Gravity.CENTER_HORIZONTAL);
            legend.setTextSize(12);
            legend.setPadding(0, 0, 0, 30);
            mainLayout.addView(legend);
        }

        // 3. Daily Logs List
        TextView historyTitle = new TextView(context);
        historyTitle.setText("Symptom & Trigger Log");
        historyTitle.setTextSize(18);
        historyTitle.setPadding(0, 30, 0, 10);
        mainLayout.addView(historyTitle);

        if (data.logs.isEmpty()) {
            TextView noLogs = new TextView(context);
            noLogs.setText("No entries found for this period.");
            mainLayout.addView(noLogs);
        } else {
            for (DailyLog log : data.logs) {
                LinearLayout logItem = new LinearLayout(context);
                logItem.setOrientation(LinearLayout.VERTICAL);
                logItem.setPadding(0, 10, 0, 15);

                TextView dateView = new TextView(context);
                dateView.setText("Date: " + log.date);
//                dateView.setTextStyle(android.graphics.Typeface.BOLD);
                logItem.addView(dateView);

                if (log.triggers != null && !log.triggers.isEmpty()) {
                    TextView triggersView = new TextView(context);
                    triggersView.setText("Triggers: " + String.join(", ", log.triggers));
                    logItem.addView(triggersView);
                } else {
                    TextView triggersView = new TextView(context);
                    triggersView.setText("Triggers: None reported");
                    triggersView.setTextColor(Color.GRAY);
                    logItem.addView(triggersView);
                }

                mainLayout.addView(logItem);
            }
        }

        return mainLayout;
    }

    private static class ZonePieChartView extends View {
        private final List<Integer> zoneData;
        private final Paint paint;
        private final android.graphics.RectF oval;

        public ZonePieChartView(Context context, List<Integer> zoneData) {
            super(context);
            this.zoneData = zoneData;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.oval = new android.graphics.RectF();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (zoneData == null || zoneData.size() < 3) return;

            float r = zoneData.get(0); // Red
            float y = zoneData.get(1); // Yellow
            float g = zoneData.get(2); // Green
            float total = r + y + g;

            if (total == 0) return; // Avoid divide by zero

            // Calculate dimensions
            float width = getWidth();
            float height = getHeight();
            float diameter = Math.min(width, height) * 0.8f; // use 80% of space
            float left = (width - diameter) / 2;
            float top = (height - diameter) / 2;

            oval.set(left, top, left + diameter, top + diameter);

            float startAngle = -90; // Start at 12 o'clock

            // Draw Red Slice
            if (r > 0) {
                float sweep = (r / total) * 360f;
                paint.setColor(Color.parseColor("#F44336"));
                canvas.drawArc(oval, startAngle, sweep, true, paint);
                startAngle += sweep;
            }

            // Draw Yellow Slice
            if (y > 0) {
                float sweep = (y / total) * 360f;
                paint.setColor(Color.parseColor("#FFC107"));
                canvas.drawArc(oval, startAngle, sweep, true, paint);
                startAngle += sweep;
            }

            // Draw Green Slice
            if (g > 0) {
                // Calculate remaining angle to ensure circle closes perfectly
                float sweep = 360f - (startAngle + 90);
                paint.setColor(Color.parseColor("#4CAF50"));
                canvas.drawArc(oval, startAngle, sweep, true, paint);
            }
        }
    }
}