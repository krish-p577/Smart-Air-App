package com.SmartAir.ParentDashboard.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class to generate a PDF report from a View or programmatically created content.
 * This class uses the native Android PdfDocument API and handles file sharing.
 */
public class ReportGenerationActivity {

    // A4 dimensions in points (72 points per inch)
    private static final int PAGE_WIDTH_POINTS = 595;
    private static final int PAGE_HEIGHT_POINTS = 842;
    private static final int MARGIN_HORIZONTAL = 40;
    private static final int MARGIN_VERTICAL = 40;

    // Hardcoded authority string - MUST MATCH THE 'android:authorities' in your manifest's FileProvider definition.
    // Example: <provider android:authorities="com.smartair.fileprovider" ... />
    public static final String FILE_PROVIDER_AUTHORITY = "com.smartair.fileprovider";

    /**
     * Generates and saves a PDF file from a given Android View.
     *
     * @param context The application context.
     * @param reportView The View containing the report content to be rendered.
     * @param fileName The base name for the output file.
     * @return The File object of the generated PDF, or null on failure.
     */
    public static File generatePdfFromView(Context context, View reportView, String fileName) {
        // 1. Prepare the View for drawing
        reportView.measure(
                View.MeasureSpec.makeMeasureSpec(PAGE_WIDTH_POINTS, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        reportView.layout(0, 0, reportView.getMeasuredWidth(), reportView.getMeasuredHeight());

        // 2. Initialize PdfDocument
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                PAGE_WIDTH_POINTS, PAGE_HEIGHT_POINTS, 1
        ).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // 3. Draw the View onto the PDF Canvas
        reportView.draw(canvas);

        // 4. Finish the page
        document.finishPage(page);

        // 5. Define output file path (using app-specific external storage)
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fullFileName = fileName + "_" + timestamp + ".pdf";

        // Use the app's internal cache directory:
        File cacheDir = new File(context.getCacheDir(), "Reports");
        if (!cacheDir.exists()) cacheDir.mkdirs();

        File outputFile = new File(cacheDir, fullFileName);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            document.writeTo(fos);
            Toast.makeText(context, "PDF saved successfully at: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        } finally {
            document.close();
        }
    }

    /**
     * Opens the Android share sheet to allow the user to share the generated PDF file.
     * NOTE: Requires defining a FileProvider in AndroidManifest.xml and an associated XML path file.
     *
     * @param context The application context.
     * @param file The PDF file to be shared.
     */
    public static void sharePdfFile(Context context, File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(context, "File not found for sharing.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get the content URI using FileProvider
            Uri contentUri = FileProvider.getUriForFile(
                    context,
                    FILE_PROVIDER_AUTHORITY,
                    file
            );

            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temp read permission to other apps

                // Start the share chooser
                context.startActivity(Intent.createChooser(shareIntent, "Share Asthma Report via..."));
            } else {
                Toast.makeText(context, "Could not generate share link.", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "Configuration error: FileProvider authority mismatch. See Logcat.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "An unexpected error occurred during sharing.", Toast.LENGTH_LONG).show();
        }
    }

    // --- Mock Data Structures and Report View Generator (Unchanged) ---

    public static class CheckIn {
        public String date;
        public String zone;
        public int rescueCount;
        public String symptoms;
        public String note;

        public CheckIn(String date, String zone, int rescueCount, String symptoms, String note) {
            this.date = date;
            this.zone = zone;
            this.rescueCount = rescueCount;
            this.symptoms = symptoms;
            this.note = note;
        }
    }

    public static View createMockReportView(Context context, List<CheckIn> checkIns) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        layout.setPadding(MARGIN_HORIZONTAL, MARGIN_VERTICAL, MARGIN_HORIZONTAL, MARGIN_VERTICAL);

        // --- Header ---
        TextView title = new TextView(context);
        title.setText("SMART AIR Provider Report");
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        layout.addView(title);

        TextView period = new TextView(context);
        period.setText("Period: Nov 11, 2024 - Nov 15, 2024");
        period.setTextSize(14);
        layout.addView(period);

        // --- Summary Section ---
        TextView summaryTitle = new TextView(context);
        summaryTitle.setText("Summary Metrics");
        summaryTitle.setTextSize(18);
        summaryTitle.setTextColor(Color.DKGRAY);
        summaryTitle.setPadding(0, 30, 0, 10);
        layout.addView(summaryTitle);

        // Calculate summary (e.g., Green/Red days, Total Rescues)
        int redDays = 0;
        int totalRescues = 0;
        for (CheckIn c : checkIns) {
            if ("Red".equals(c.zone)) redDays++;
            totalRescues += c.rescueCount;
        }

        TextView summaryText = new TextView(context);
        summaryText.setText(String.format(Locale.US,
                "Total Days: %d\nRed Zone Days: %d\nTotal Rescue Inhaler Uses: %d",
                checkIns.size(), redDays, totalRescues));
        summaryText.setTextSize(16);
        layout.addView(summaryText);

        // --- Daily Log Section ---
        TextView logTitle = new TextView(context);
        logTitle.setText("Daily Log Entries");
        logTitle.setTextSize(18);
        logTitle.setTextColor(Color.DKGRAY);
        logTitle.setPadding(0, 30, 0, 10);
        layout.addView(logTitle);

        for (CheckIn entry : checkIns) {
            TextView entryView = new TextView(context);
            String entryText = String.format(Locale.US,
                    "• %s | Zone: %s | Rescues: %d\n  Symptoms: %s\n  Note: %s",
                    entry.date, entry.zone, entry.rescueCount, entry.symptoms, entry.note);
            entryView.setText(entryText);
            entryView.setTextSize(12);
            entryView.setPadding(0, 5, 0, 5);

            // Highlight based on zone (simple color indicator)
            if ("Red".equals(entry.zone)) {
                entryView.setBackgroundColor(0xFFFFEEEE); // Very Light Red
            } else if ("Yellow".equals(entry.zone)) {
                entryView.setBackgroundColor(0xFFFFFFEE); // Very Light Yellow
            }
            layout.addView(entryView);
        }

        return layout;
    }
}