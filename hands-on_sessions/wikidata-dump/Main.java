import Analysis.ItemsAnalyzer;
import EDU.oswego.cs.dl.util.concurrent.FJTask;
import Model.Translation;
import Reading.UserProfilesReader;
import Reading.WikidataSampleHistoryToCSV;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import preprocessing.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

public class Main {
    // SimpleThreadPool

    public static void main(String[] args) {


        testCommonsCSV();

       try {
           int difference = difference(lastTimestamp,currentTimestamp);
           System.out.println("difference is: "+difference);
       }
       catch(Exception e)
       {e.printStackTrace();}




    }
    public static void testCommonsCSV()
    {

        ArrayList<String> elements = new ArrayList<String>();
        //String ec = StringUtils.normalizeSpace(es);
        String es = "aaa\t";
        String ec = StringUtils.deleteWhitespace(es);
        ec = StringEscapeUtils.unescapeCsv(es);
        elements.add(ec);
        es = "a\\";
        ec = StringUtils.chop(es);

        elements.add(ec);
        es = "ÜÜwef";

        elements.add(ec);
        es = "aaa\\n";


        elements.add(ec);


        File file = new File("testTSV.csv");
        try{

            CSVPrinter csvFilePrinter = null;
           // CSVFormat csvFileFormat = CSVFormat.TDF.withHeader();
            CSVFormat csvFileFormat = CSVFormat.TDF;
            FileWriter fileWriter = new FileWriter(file);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

            csvFilePrinter.printRecord(elements);


            fileWriter.flush();
            fileWriter.close();
            csvFilePrinter.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}



