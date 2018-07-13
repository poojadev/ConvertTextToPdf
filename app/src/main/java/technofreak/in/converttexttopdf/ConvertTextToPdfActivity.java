package technofreak.in.converttexttopdf;


import android.app.Dialog;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConvertTextToPdfActivity extends AppCompatActivity implements View.OnClickListener{


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] skillNamesStringArray;
    String[] skillDescriptionStringArray;
    EditText etSkillTitle;
    EditText etSkillName;
    EditText etName;
    EditText etEmail;
    FloatingActionButton fbRemoveskills;
    LinearLayout lvSkillNames;
    File file;
    File myPDFFile;
    List<EditText> skillNameEditText = new ArrayList<EditText>();
    List<EditText> skillDescriptionEditText = new ArrayList<EditText>();
    Document document = new Document(PageSize.A4,30,30,30,30);
    Button btSubmit;
    FloatingActionButton fbAddNewSkills;
     View addView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converttexttopdf_activity);
        initView();


      if( checkAndRequestPermissions())
      {
     /*
       create internal directory
   */
          String newFolder = "/ConvertedPDF";
          String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
          File myNewFolder = new File(extStorageDirectory + newFolder);
          myNewFolder.mkdirs();
          file= new File(extStorageDirectory + newFolder);
          file.mkdirs();

      }

    }








public void initView()
{

    etName=(EditText)findViewById(R.id.convertTextPDF_activity_etName);
    etEmail=(EditText)findViewById(R.id.convertTextPDF_activity_etEmail);
    fbAddNewSkills=(FloatingActionButton)findViewById(R.id.convertTextPDF_activity_fb_addNew);
    fbAddNewSkills.setOnClickListener(this);
    btSubmit=(Button)findViewById(R.id.convertTextPDF_activity_btSubmit);
    btSubmit.setOnClickListener(this);


//    fbRemoveskills.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            ((LinearLayout) addView.getParent()).removeView(addView);
//
//        }
//    });

}

/*
   check permissions
 */
    private  boolean checkAndRequestPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.convertTextPDF_activity_btSubmit:
                if(checkValidation()) {
                    try {
                        new ConvertToPdfTask().execute();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.convertTextPDF_activity_fb_addNew:

                lvSkillNames = (LinearLayout) findViewById(R.id.convertTextPDF_activity_lv_skills);
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.skills_dynamic_layout, null);
                etSkillTitle = (EditText) addView.findViewById(R.id.skills_DynamicLayout_etSkillTitles);
                etSkillName = (EditText) addView.findViewById(R.id.skills_DynamicLayout_etSkillName);
                lvSkillNames.addView(addView);


                skillNameEditText.add(etSkillName);
                skillDescriptionEditText.add(etSkillTitle);
    /*

     */
                skillNamesStringArray = new String[skillNameEditText.size()];
                skillDescriptionStringArray = new String[skillDescriptionEditText.size()];
                fbRemoveskills = (FloatingActionButton) addView.findViewById(R.id.skills_DynamicLayout_fb_remove);
                fbRemoveskills.setOnClickListener(this);
                fbRemoveskills.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((LinearLayout) addView.getParent()).removeView(addView);

                    }
                });

                break;


        }

        }
    /*

   asynctask to convert text data to PDF
    */

    private boolean checkValidation() {
        String strName = etName.getText().toString();
        String strTitle = etEmail.getText().toString();

        if (strName.trim().isEmpty()) {
            etName.requestFocus();
            etName.setError("Please Enter Your Name.");
            return false;
        }  else if (strTitle.trim().isEmpty()) {
            etEmail.requestFocus();
            etEmail.setError("Please Enter Title");
            etEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            return false;

        } else {
            etName.setError(null);
            etEmail.setError(null);
            return true;
        }

    }


    //Step 3
//            switch (flag) {
//                case 2:

//                    break;
//
//
//            }

    //Step 4 Add content
//            switch (flag) {
//                case 2:

    /*
       code to generate PDF
        */
    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        //   cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

public   void showSuccessDialog(String pdfPath) {
    // Create custom dialog object
    final Dialog dialog = new Dialog(ConvertTextToPdfActivity.this);
    // Include dialog.xml file
      dialog.setContentView(R.layout.custom_dialog);
    // Set dialog title
    dialog.setTitle("PDF Created");
    dialog.setCancelable(false);
    dialog.setCanceledOnTouchOutside(false);

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(dialog.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    lp.gravity = Gravity.CENTER;

    dialog.getWindow().setAttributes(lp);
    // set values for custom dialog components - text, image and button
    TextView tvSuccess = (TextView) dialog.findViewById(R.id.custom_dialog_textDialog);
    tvSuccess.setText("Please check your PDF at" + " " +pdfPath);


    dialog.show();

    Button btOk = (Button) dialog.findViewById(R.id.custom_dialog_okButton);
    // if decline button is clicked, close the custom dialog
    btOk.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Close dialog
            dialog.dismiss();
            finish();
        }
    });
}

    public class ConvertToPdfTask extends AsyncTask<String, String, String> {
       // Activity activity;

        public ConvertToPdfTask() throws DocumentException {
            //this.activity = a;
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Date date = new Date();
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
            myPDFFile = new File(file  + "/"+"ConvertedPDF" + " _"+ timeStamp + ".pdf"); //this will store the PDF file into  ConvertedPDF folder  with unique Name
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(myPDFFile);
                Log.e("output",output.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //Step 1


            Paragraph preface = new Paragraph();

            try {
                //  pdfWriter.getInstance(document, output);

                PdfWriter writer = PdfWriter.getInstance(document, output); //document is Document object and output is the pdf file we have created

                PDFEvents event = new PDFEvents();
                writer.setPageEvent(event);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            //  break;
            document.open();
            try {
                document.add(new Paragraph("   "));
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(),  R.drawable.pdf);
            BitmapDrawable bitDw = ((BitmapDrawable) vectorDrawable);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);


            Image image = null;
            try {
                image = Image.getInstance(stream.toByteArray());

                image.setAlignment(Element.ALIGN_CENTER);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
               document.add(image);
            } catch (DocumentException e) {
                e.printStackTrace();
            }


            preface.setAlignment(Element.ALIGN_CENTER);


//
            Font blue = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
            Chunk title = new Chunk("Personal Information", blue);


            try {
                document.add(new Paragraph(title));
                Chunk linebreak = new Chunk(new LineSeparator(2f, 100f, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -1));
                    document.add(linebreak);
                    /*
                       chunk  is the smallest significant part of text that can be added to a document.
                        Most elements can be divided in one or more Chunks. A chunk is a String with a certain Font
                     */

                document.add(new Paragraph("   ")); //this will add blank Paragraph or space
                document.add(preface = new Paragraph("Name: " +
                        "  "+ etName.getText().toString() + " \n "
                        + "Email:" + "  " + etEmail.getText().toString()));
                document.add(new Paragraph("   "));

            } catch (DocumentException e) {
                e.printStackTrace();
            }



            skillNamesStringArray = new String[skillNameEditText.size()];
            skillDescriptionStringArray = new String[skillDescriptionEditText.size()];
            float[] columnWidths = {1.5f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            Font bfBold12 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(0, 0, 0));
            //insert column headings
            insertCell(table, "Skills", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Description", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);
            for (int i = 0; i < skillNameEditText.size(); i++) {

                skillNamesStringArray[i] = skillNameEditText.get(i).getText().toString();
                skillDescriptionStringArray[i] = skillDescriptionEditText.get(i).getText().toString();
                insertCell(table, skillNamesStringArray[i], Element.ALIGN_RIGHT, 1, bfBold12);
                insertCell(table, skillDescriptionStringArray[i], Element.ALIGN_CENTER, 2, bfBold12);
            }
            try {
                document.add(table);

            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.close();

            showSuccessDialog(myPDFFile.getPath());
        }


    }

    /*
       function to create success dialog
     */

    /*
    class  for creating PDF
    */

    private Image total;

    class PDFEvents extends PdfPageEventHelper {
        private PdfTemplate t;
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 15, Font.NORMAL);

        public void onOpenDocument(PdfWriter writer, Document document) {
            t = writer.getDirectContent().createTemplate(30, 16);
            try {
                total = Image.getInstance(t);
                total.setAlignment(PdfName.DICTIONARY);

            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle rect = document.getPageSize();
            rect.setBorder(Rectangle.BOX); // left, right, top, bottom border
            rect.setBorderWidth(5); // a width of 5 user units
            rect.setBorderColor(BaseColor.RED); // a red border
            rect.setUseVariableBorders(true); // the full width will be visible
            canvas.rectangle(rect);
            /*add header to your PDF*/
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("Example to Convert Text to PDF", ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    header,
                    (document.left()-55)  + document.left(),
                    document.top() , 0);
            Chunk linebreak = new Chunk(new LineSeparator(2f, 100f, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -1));
            header.add(linebreak);


            addFooter(writer);
        }


        public void onCloseDocument(PdfWriter writer, Document document) {
            /*
            add page count to your PDF
             */
            int totalLength = String.valueOf(writer.getPageNumber()).length();
            int totalWidth = totalLength * 5;
            ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)),
                    totalWidth, 6, 0);
        }





        private void addFooter(PdfWriter writer){
            PdfPTable footer = new PdfPTable(3);

            try {
                // set defaults
                footer.setWidths(new int[]{24, 2, 1});
                footer.setTotalWidth(527);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setFixedHeight(40);
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

                // add copyright
                footer.addCell(new Phrase("Technofreak", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

                // add current page count
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)));

                // add placeholder for total page count
                PdfPCell totalPageCount = new PdfPCell(total);
                totalPageCount.setBorder(Rectangle.TOP);
                totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
                footer.addCell(totalPageCount);

                // write page
                PdfContentByte canvas = writer.getDirectContent();
              //  canvas.beginMarkedContentSequence(PdfName._3D);
                footer.writeSelectedRows(0, -1, 34, 50, canvas);
//                canvas.endMarkedContentSequence();
            } catch(DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

    }
}
