package com.SmartAir.history.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import android.content.Context;

import com.SmartAir.history.presenter.HistoryItem;
import com.SmartAir.history.HistoryContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Utility class responsible for exporting HistoryItem data into a PDF file.
 */
public class DataToPdfExporter implements HistoryContract.Export{

    private final Context context;
    public DataToPdfExporter(Context context){
        this.context = context;
    }

    @Override
    public void exportHistoryToPdf(List<HistoryItem> items){
        PdfDocument pdfDocument = new PdfDocument();

        String timestamp = String.valueOf(System.currentTimeMillis());
        final int pageWidth = 595;
        final int pageHeight = 842;
        final int margin = 40;
        final int lineHeight = 18;

        Paint paint = new Paint();

        int pageNumber = 1;
        int y = margin;

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // bold title and make bigger
        paint.setTextSize(18f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Daily Check-In Symptoms History", margin, y, paint);

        // regular text size
        paint.setTextSize(14f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        y += lineHeight * 2;

        // design pdf
        for (HistoryItem item : items) {

            // if out of room make new page
            if (y > pageHeight - margin * 2) {
                pdfDocument.finishPage(page);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber)
                        .create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = margin;
            }

            canvas.drawText("Date: " + item.getDate(), margin, y, paint);
            y += lineHeight;
            canvas.drawText("Entry Author: " + item.getEntryAuthor(), margin, y, paint);
            y += lineHeight;
            canvas.drawText("Child: " + item.getChildName(), margin, y, paint);
            y += lineHeight;
            canvas.drawText("Night Waking: " +
                            (Boolean.TRUE.equals(item.getNightWaking()) ? "True" : "False"), margin, y,
                    paint);
            y += lineHeight;
            canvas.drawText("Limited Ability: " +
                            (Boolean.TRUE.equals(item.getLimitedAbility()) ? "True" : "False"), margin, y,
                    paint);
            y += lineHeight;
            canvas.drawText("Cough/Wheeze: " +
                    (Boolean.TRUE.equals(item.getSick()) ? "True" : "False"), margin, y, paint);
            y += lineHeight;

            String triggersText =
                    (item.getTriggers() == null || item.getTriggers().isEmpty())
                            ? "None"
                            : TextUtils.join(", ", item.getTriggers());
            canvas.drawText("Triggers: " + triggersText, margin, y, paint);
            y += lineHeight * 2;
        }

        pdfDocument.finishPage(page);

        // Save to local device
        File pdfDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                ;
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "history_export_" + timestamp + ".pdf";
        File pdfFile = new File(pdfDir, fileName);

        // Toast that explains where saved
        try (FileOutputStream temp = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(temp);

            Toast.makeText(
                    context,
                    "Filtered Daily Check-ins saved as PDF to Downloads",
                    Toast.LENGTH_LONG
            ).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    "Failed to save PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {
            pdfDocument.close();
        }
    }
}
