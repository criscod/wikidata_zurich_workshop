package Reading;

import Model.XMLElementProperties;
import Model.XMLElementState;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by csarasua.
 */
public class WikidataPageHistoryReader extends DefaultHandler {


    XMLElementState currentState=null;
    XMLElementProperties currentProperty=null;

    File pagesFile;
    File revisionsFile;
    File uploadsFile;
    File contributorsFile;

    StringBuilder pageTitleB= new StringBuilder();
    StringBuilder pageNsB = new StringBuilder();
    StringBuilder pageIdB = new StringBuilder();

    ICsvListWriter listWriterP = null;
    ICsvListWriter listWriterC = null;
    ICsvListWriter listWriterR = null;

    final CellProcessor[] pagesProcessors = getPagesProcessors();
    final CellProcessor[] contributorsProcessors = getContributorsProcessors();
    final CellProcessor[] revisionsProcessors = getRevisionsProcessors();

    /*
    StringBuilder pageRestrictionsB= new StringBuilder();
    StringBuilder pageRedirectsB = new StringBuilder();
    */

    StringBuilder revSha1B= new StringBuilder();
    StringBuilder revModelB= new StringBuilder();
    StringBuilder revFormatB= new StringBuilder();
    StringBuilder revCommB = new StringBuilder();
    StringBuilder revParentIdB = new StringBuilder();
    StringBuilder revTimestampB = new StringBuilder();
    StringBuilder revMinorB = new StringBuilder();
    StringBuilder revIdB = new StringBuilder();

    StringBuilder upFileNameB= new StringBuilder();
    StringBuilder upCommB = new StringBuilder();

    StringBuilder contribUsernameB= new StringBuilder();
    StringBuilder contribIdB = new StringBuilder();
    StringBuilder contribIpB = new StringBuilder();

    boolean revCommDeletedFlag=false;
    boolean upCommDeletedFlag=false;

    String redirectTitle=null;

    ArrayList<String> pages = new ArrayList<String>();
    ArrayList<String> revisions = new ArrayList<String>();
    ArrayList<String> uploads = new ArrayList<String>();
    ArrayList<String> contributors = new ArrayList<String>();


    int iRevs = 0;

    String pageTitle = "unknown";
    String pageNs = "unknown";
    String pageId = "unknown";
    String pageRedirect = "unknown";
    String pageRestrictions = "unknown";
    String pageDiscussion="unknown";

    String revId = "unknown";
    String revParentId="unknown";
    String revTimestamp = "unknown";
    String revMinor="unknown";
    String revComment="unknown";
    String revText="notparsed";
    String revSha1="unknown";
    String revModel="unknown";
    String revFormat="unknown";


    String upTimestamp="unknown";
    String upComment="unknown";
    String upFileName="unknown";
    String upSrc="unknown";
    String upSize="unknown";


    String contribId = "unknown";
    String contribUserName="unknown";
    String contribIp="unknown";





    String workingDir;
    String workingDirForFileName;



    HashMap<String,Integer> users = new HashMap<String,Integer>();



    private void loadUsers()
    {
        File f = new File("C:/Users/csarasua/Documents/wikidataanalysis/src/main/resources/users.txt");

        try {
            List<String> lines = Files.readLines(f, Charsets.UTF_8);
            for(String user: lines)
            {
                users.put(user, new Integer(1));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static CellProcessor[] getRevisionsProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull(),
                new NotNull()
        };

        return processors;
    }

    private static CellProcessor[] getPagesProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull(),
                new NotNull()
        };

        return processors;
    }

    private static CellProcessor[] getContributorsProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull(),
                new NotNull()
        };

        return processors;
    }
    public WikidataPageHistoryReader(String inputHistoryFile)
    {

       // loadUsers(); to retrieve the edits of a particular set of users (e.g. the 10 top-k based on # total edits)

       // workingDir = System.getProperty("user.dir");
        workingDir = System.getProperty("user.home");
        workingDirForFileName = workingDir.replace("\\", "/");


        Path p = Paths.get(inputHistoryFile);
        String localNameOfXMLFile = p.getFileName().toString();


        String ls = System.getProperty("line.separator");




        /*
        * GZIPInputStream in = null;


        try {
            in = new GZIPInputStream(new FileInputStream(this.filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */



        try {

            /*
            pagesFile = new File(workingDirForFileName + "/src/main/resources/pages_" + localNameOfXMLFile + ".csv");
            Files.write("\"pageTitle\",\"pageNs\",\"pageId\",\"pageRedirect\",\"pageRestrictions\",\"pageDiscussion\"", pagesFile, Charsets.UTF_8);
            Files.append(ls, pagesFile, Charsets.UTF_8);
            revisionsFile = new File(workingDirForFileName + "/src/main/resources/revisions_" + localNameOfXMLFile + ".csv");
            Files.write("\"revId\",\"revParentid\",\"revTimestamp\",\"revMinor\",\"revComment\",\"revText\",\"revSha1\",\"revModel\",\"revFormat\",\"revContributor\",\"revPage\"", revisionsFile, Charsets.UTF_8);
            Files.append(ls, revisionsFile, Charsets.UTF_8);
            uploadsFile = new File(workingDirForFileName + "/src/main/resources/uploads_" + localNameOfXMLFile + ".csv");
            Files.write("\"upTimestamp\",\"upComment\",\"upFileName\",\"upSrc\",\"upSize\",\"upPage\",\"upContributor\"", uploadsFile, Charsets.UTF_8);
            Files.append(ls, uploadsFile, Charsets.UTF_8);
            contributorsFile = new File(workingDirForFileName + "/src/main/resources/contributors_" + localNameOfXMLFile + ".csv");
            Files.append(ls, contributorsFile, Charsets.UTF_8);
            */

            // (for all) pagesFile = new File("E:/wikidata_data/pages_" + localNameOfXMLFile + ".csv");





            /*
            workingDirForFileName = "/data/ubuntu";
            pagesFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/pages_" + localNameOfXMLFile + ".csv");
            //Files.write("\"pageTitle\",\"pageNs\",\"pageId\",\"pageRedirect\",\"pageRestrictions\",\"pageDiscussion\"", pagesFile, Charsets.UTF_8);


            Files.write("\"pageTitle\",\"pageNs\",\"pageId\",\"pageRedirect\"", pagesFile, Charsets.UTF_8);

            Files.append(ls, pagesFile, Charsets.UTF_8);
            revisionsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/revisions_"+ localNameOfXMLFile + ".csv");
            Files.write("\"revId\",\"revParentid\",\"revTimestamp\",\"revMinor\",\"revComment\",\"revText\",\"revSha1\",\"revModel\",\"revFormat\",\"revContributor\",\"revPage\"", revisionsFile, Charsets.UTF_8);
            Files.append(ls, revisionsFile, Charsets.UTF_8);
            uploadsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/uploads_" + localNameOfXMLFile + ".csv");
            Files.write("\"upTimestamp\",\"upComment\",\"upFileName\",\"upSrc\",\"upSize\",\"upPage\",\"upContributor\"", uploadsFile, Charsets.UTF_8);
            Files.append(ls, uploadsFile, Charsets.UTF_8);
            contributorsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/contributors_"+ localNameOfXMLFile + ".csv");
            Files.write("\"contribId\",\"contribUsername\",\"contribIp\"", contributorsFile, Charsets.UTF_8);
            Files.append(ls, contributorsFile, Charsets.UTF_8);


            */



            workingDirForFileName = "/data/ubuntu";
            pagesFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/pages_" + localNameOfXMLFile + ".csv");
            listWriterP = new CsvListWriter(new FileWriter(pagesFile),new CsvPreference.Builder('"', '\t', "\n").build());
                    //CsvPreference.TAB_PREFERENCE);
            final String[] headerP = new String[] { "pageTitle", "pageNs", "pageI"};
            // write the header
            listWriterP.writeHeader(headerP);

            // Files.write("pageTitle\ttpageNs\tpageId\tpageRedirect", pagesFile, Charsets.UTF_8);
           // Files.append(ls, pagesFile, Charsets.UTF_8);
            revisionsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/revisions_"+ localNameOfXMLFile + ".csv");
            listWriterR = new CsvListWriter(new FileWriter(revisionsFile),new CsvPreference.Builder('"', '\t', "\n").build());
                    //CsvPreference.TAB_PREFERENCE);
            final String[] headerR = new String[] {"revId", "revParentid", "revTimestamp", "revMinor", "revComment", "revText", "revSha1", "revModel", "revFormat", "revContributor", "revPage"};
            // write the header
            listWriterR.writeHeader(headerR);
            //Files.write("revId\trevParentid\trevTimestamp\trevMinor\trevComment\trevText\trevSha1\trevModel\trevFormat\trevContributor\trevPage", revisionsFile, Charsets.UTF_8);
           // Files.append(ls, revisionsFile, Charsets.UTF_8);
            uploadsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/uploads_" + localNameOfXMLFile + ".csv");
           // Files.write("upTimestamp\tupComment\tupFileName\tupSrc\tupSize\tupPage\tupContributor", uploadsFile, Charsets.UTF_8);
           // Files.append(ls, uploadsFile, Charsets.UTF_8);
            contributorsFile = new File(workingDirForFileName+"/dumps.wikimedia.org/wikidatawiki/20160701/contributors_"+ localNameOfXMLFile + ".csv");
            listWriterC = new CsvListWriter(new FileWriter(contributorsFile),new CsvPreference.Builder('"', '\t', "\n").build());
                   // CsvPreference.TAB_PREFERENCE);
            final String[] headerC = new String[] {"contribId", "contribUsername", "contribIp"};
            // write the header
            listWriterC.writeHeader(headerC);
           // Files.write("contribId\tcontribUsername\tcontribIp", contributorsFile, Charsets.UTF_8);
           // Files.append(ls, contributorsFile, Charsets.UTF_8);


            /*
            pagesFile = new File(workingDirForFileName+"/src/main/resources/pages_" + localNameOfXMLFile + ".csv");

            Files.write("pageTitle\ttpageNs\tpageId\tpageRedirect", pagesFile, Charsets.UTF_8);
            Files.append(ls, pagesFile, Charsets.UTF_8);
            revisionsFile = new File(workingDirForFileName+"/src/main/resources/revisions_"+ localNameOfXMLFile + ".csv");
            Files.write("revId\trevParentid\trevTimestamp\trevMinor\trevComment\trevText\trevSha1\trevModel\trevFormat\trevContributor\trevPage", revisionsFile, Charsets.UTF_8);
            Files.append(ls, revisionsFile, Charsets.UTF_8);
            uploadsFile = new File(workingDirForFileName+"/src/main/resources/uploads_" + localNameOfXMLFile + ".csv");
            Files.write("upTimestamp\tupComment\tupFileName\tupSrc\tupSize\tupPage\tupContributor", uploadsFile, Charsets.UTF_8);
            Files.append(ls, uploadsFile, Charsets.UTF_8);
            contributorsFile = new File(workingDirForFileName+"/src/main/resources/contributors_"+ localNameOfXMLFile + ".csv");
            Files.write("contribId\tcontribUsername\tcontribIp", contributorsFile, Charsets.UTF_8);
            Files.append(ls, contributorsFile, Charsets.UTF_8);
            */







/*
            pagesFile = new File("E:/wikidata_data/pages_" + localNameOfXMLFile + ".csv");
           // Files.write("\"pageTitle\",\"pageNs\",\"pageId\",\"pageRedirect\",\"pageRestrictions\",\"pageDiscussion\"", pagesFile, Charsets.UTF_8);
            Files.write("\"pageTitle\",\"pageNs\",\"pageId\"", pagesFile, Charsets.UTF_8);

            Files.append(ls, pagesFile, Charsets.UTF_8);
            revisionsFile = new File("E:/wikidata_data/revisions_"+ localNameOfXMLFile + ".csv");
            Files.write("\"revId\",\"revParentid\",\"revTimestamp\",\"revMinor\",\"revComment\",\"revText\",\"revSha1\",\"revModel\",\"revFormat\",\"revContributor\",\"revPage\"", revisionsFile, Charsets.UTF_8);
            Files.append(ls, revisionsFile, Charsets.UTF_8);
            uploadsFile = new File("E:/wikidata_data/uploads_" + localNameOfXMLFile + ".csv");
            Files.write("\"upTimestamp\",\"upComment\",\"upFileName\",\"upSrc\",\"upSize\",\"upPage\",\"upContributor\"", uploadsFile, Charsets.UTF_8);
            Files.append(ls, uploadsFile, Charsets.UTF_8);
            contributorsFile = new File("E:/wikidata_data/contributors_"+ localNameOfXMLFile + ".csv");
            Files.write("\"contribId\",\"contribUsername\",\"contribIp\"", contributorsFile, Charsets.UTF_8);
            Files.append(ls, contributorsFile, Charsets.UTF_8);
            */



        } catch (IOException e) {
        e.printStackTrace();
    }



        currentState = XMLElementState.DefaultState;
        currentProperty = XMLElementProperties.defaultP;

    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {


        //  if (qName.equalsIgnoreCase("page"))
        switch(qName)
        {

            case "page":
                currentState = XMLElementState.Page;
                break;
            case "title":
                pageTitleB.setLength(0);
                currentProperty= XMLElementProperties.pageTitleP;
                break;
            case "ns":
                pageNsB.setLength(0);
                currentProperty= XMLElementProperties.pageNsP;
                break;
            case "id":

                if(currentState.equals(XMLElementState.Page))
                {
                    pageIdB.setLength(0);
                    currentProperty= XMLElementProperties.pageIdP;
                }
                else if(currentState.equals(XMLElementState.Contributor))
                {
                    contribIdB.setLength(0);
                    currentProperty= XMLElementProperties.contribIdP;

                }
                else if(currentState.equals(XMLElementState.Revision))
                {
                    iRevs++;
                    revIdB.setLength(0);
                    currentProperty = XMLElementProperties.revIdP;
                }
                break;
          /*  case "redirect":
                redirectTitle=attributes.getValue("title");
                pageRedirectsB.setLength(0);
                currentProperty= XMLElementProperties.pageRedirectP;
                break;
            case "restrictions":
                pageRestrictionsB.setLength(0);
                currentProperty= XMLElementProperties.pageRestrictionsP;
                break;*/
            case "revision":
                currentState=XMLElementState.Revision;
                break;
            case "upload":
                currentState=XMLElementState.Upload;
                break;
           /* case "discussionthreadinginfo":
                currentProperty= XMLElementProperties.pageDiscussionP;
                break;*/
            case "parentid":
                revParentIdB.setLength(0);
                currentProperty= XMLElementProperties.revParentIdP;
                break;
            case "timestamp":
                if(currentState.equals(XMLElementState.Revision))
                {
                    revTimestampB.setLength(0);
                    currentProperty= XMLElementProperties.revTimestampP;
                }
                else if(currentState.equals(XMLElementState.Upload))
                {
                    currentProperty= XMLElementProperties.upTimestampP;
                }
                break;
            case "contributor":
                currentState=XMLElementState.Contributor;
                break;
            case "minor":
                revMinorB.setLength(0);
                currentProperty= XMLElementProperties.revMinorP;
                break;
            case "comment":
               // revCommB.setLength(0);
               // upCommB.setLength(0);

                if(currentState.equals(XMLElementState.Revision))
                {
                    revCommB.setLength(0);
                    currentProperty= XMLElementProperties.revCommentP;
                    if(attributes.getValue("deleted")!=null)
                    {
                        revCommDeletedFlag=true;
                    }
                }
                else if(currentState.equals(XMLElementState.Upload))
                {
                    upCommB.setLength(0);
                    currentProperty= XMLElementProperties.upCommentP;
                    if(attributes.getValue("deleted")!=null)
                    {
                        upCommDeletedFlag=true;
                    }
                }

                break;
            case "text":
                currentProperty= XMLElementProperties.revTextP;
                break;
            case "sha1":
                revSha1B.setLength(0);
                currentProperty= XMLElementProperties.revSha1P;
                break;
            case "model":
                revModelB.setLength(0);
                currentProperty= XMLElementProperties.revModelP;
                break;
            case "format":
                revFormatB.setLength(0);
                currentProperty= XMLElementProperties.revFormatP;
                break;
            case "filename":
                upFileNameB.setLength(0);
                currentProperty= XMLElementProperties.upFileNameP;
                break;
            case "src":
                currentProperty= XMLElementProperties.upSrcP;
                break;
            case "size":
                currentProperty= XMLElementProperties.upSizeP;
                break;
            case "username":
                contribUsernameB.setLength(0);
                currentState=XMLElementState.Contributor;
                currentProperty= XMLElementProperties.contribUserNameP;
                break;
            case "ip":
                contribIpB.setLength(0);
                currentProperty= XMLElementProperties.contribIpP;
                break;
        }


    }


    private void printWithOPenCSVPrinter(File file, ArrayList<String> elements)
    {


        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()), '\t');

        // feed in your array (or convert your data to an array)
            String[] entries = elements.toArray(new String[elements.size()]);
        writer.writeNext(entries);
        writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printWithGuava(File file, ArrayList<String> elements)
    {

        ArrayList<String> cleanElements = new ArrayList<String>();
        for (String e: elements)
        {
            cleanElements.add(cleanString(e));
        }



        try {
        if (file.getName().startsWith("pages"))
        {

                listWriterP.write(cleanElements, pagesProcessors);

        }
        else if (file.getName().startsWith("revisions"))
        {
            listWriterR.write(cleanElements, revisionsProcessors);
        }
        else if (file.getName().startsWith("contributors"))
        {
            listWriterC.write(cleanElements, contributorsProcessors);
        }

        /*//TODO: should have written with escapeCsv! to deal with ,""username"",
        String ls = System.getProperty("line.separator");
        try {


            //writes header of result file
            int count=0;
            int last = elements.size()-1;
            StringBuilder lineB = new StringBuilder();


            String line=null;
            String el=null;
            for (String e: elements)
            {

                el = e.trim();

                if(count==last)
                {

                    lineB.append(el);
                    //line=el;
                    //line ="\""+el+"\"";

                }
                else
                {
                    lineB.append(el+"\t");
                  // line=el+"\t";
                    //line="\""+el+"\",";
                }
                line = lineB.toString();




                count++;

            }





            Files.append(line, file, Charsets.UTF_8);
            Files.append(ls, file, Charsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();

        }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void print(File file, ArrayList<String> elements)
    {
       try{

           CSVPrinter csvFilePrinter = null;
           CSVFormat csvFileFormat = CSVFormat.TDF.withHeader();
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




    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {






        if (qName.equalsIgnoreCase("page")) {
            pages.clear();
            if(pageTitleB.length()!=0 &&!pageTitleB.toString().startsWith("\n")) {
                pageTitle = cleanString(pageTitleB.toString());
            }
            if(pageNsB.length()!=0 &&!pageNsB.toString().startsWith("\n")) {
                pageNs = pageNsB.toString();
            }
            if(pageIdB.length()!=0 &&!pageIdB.toString().startsWith("\n")) {
                pageId = pageIdB.toString();
            }

            pages.add(pageTitle);
            pages.add(pageNs);
            pages.add(pageId);
            //pageRedirectsB.append("**redirect title**"+redirectTitle);
           /* pageRedirect=pageRedirectsB.toString();
            pages.add(pageRedirect);
            pageRestrictions = pageRestrictionsB.toString();
            pages.add(pageRestrictions);
            pages.add(pageDiscussion);*/
            //write the page in file


            printWithGuava(pagesFile,pages);


            //reset all strings for page

            pageTitle = "unknown";
            pageNs = "unknown";
            pageId = "unknown";
            pageRedirect = "unknown";
            pageRestrictions = "unknown";
            pageDiscussion="unknown";

            pageTitleB.setLength(0);
            pageNsB.setLength(0);
            pageIdB.setLength(0);


        }
        else if (qName.equalsIgnoreCase("revision")) {
            revisions.clear();
            if(revIdB.length()!=0 &&!revIdB.toString().startsWith("\n")) {
                revId = revIdB.toString();
            }
            revisions.add(revId);
            if(revParentIdB.length()!=0 &&!revParentIdB.toString().startsWith("\n")) {
                revParentId = revParentIdB.toString();
            }
            revisions.add(revParentId);
            if(revTimestampB.length()!=0 &&!revTimestampB.toString().startsWith("\n")) {
                revTimestamp = revTimestampB.toString();
            }
            revisions.add(revTimestamp);
            if(revMinorB.length()!=0 &&!revMinorB.toString().startsWith("\n")) {
                revMinor = revMinorB.toString();
            }
            revisions.add(revMinor);
            
            if(revCommB.length()!=0 &&!revCommB.toString().startsWith("\n")) {
                revComment= cleanString(revCommB.toString());
            }

            currentProperty= XMLElementProperties.defaultP; // not really needed now


            revisions.add(revComment);
            revisions.add(revText);

            if(revSha1B.length()!=0 && !revSha1B.toString().startsWith("\n")) {
                revSha1 = revSha1B.toString();
            }
            revisions.add(revSha1);
            if(revModelB.length()!=0 && !revModelB.toString().startsWith("\n")) {
                revModel = revModelB.toString();
            }
            revisions.add(revModel);
            if(revFormatB.length()!=0 && !revFormatB.toString().startsWith("\n")) {
                revFormat = revFormatB.toString();
            }
            revisions.add(revFormat);

            //backlinks to page and contributor
            //contribIpB.length()!=0 && !contribIpB.toString().startsWith("\n") &&

            if((contribIdB.length()==0)&& (contribIpB.length()!=0))
           {
               contribIp = contribIpB.toString();
               revisions.add(contribIp);
           }
            else {
               contribId = contribIdB.toString();
               revisions.add(contribId);
           }

            if(pageIdB.length()!=0 && !pageIdB.toString().startsWith("\n")) {
                pageId = pageIdB.toString();
            }

           

            revisions.add(pageId);

            printWithGuava(revisionsFile,revisions);

            revId = "unknown";
            revParentId="unknown";
            revTimestamp = "unknown";
            revMinor="unknown";
            revComment="unknown";
            revText="notparsed";
            revSha1="unknown";
            revModel="unknown";
            revFormat="unknown";

            revIdB.setLength(0);
            revParentIdB.setLength(0);
            revTimestampB.setLength(0);
            revMinorB.setLength(0);
            revCommB.setLength(0);
            revSha1B.setLength(0);
            revModelB.setLength(0);
            revFormatB.setLength(0);

            currentState=XMLElementState.Page;
            currentProperty=XMLElementProperties.defaultP;

            contribId="unknown";
            contribIp="unknown";
            contribUserName="unknown";

            contribIdB.setLength(0);
            contribIpB.setLength(0);
            contribUsernameB.setLength(0);


        }
        else if (qName.equalsIgnoreCase("upload")) {
            uploads.clear();

            uploads.add(upTimestamp);
            if(upCommDeletedFlag)
            {
                upCommB.append("**deletedFlag**");
            }
            upComment = upCommB.toString();


            uploads.add(upComment);
            if(upFileNameB.length()!=0 && !upFileNameB.toString().startsWith("\n")) {
                upFileName = upFileNameB.toString();
            }
            uploads.add(upFileName);
            uploads.add(upSrc);
            uploads.add(upSize);

            //backlinks to page and contributor
            uploads.add(pageId);
            uploads.add(contribId);

            printWithGuava(uploadsFile,uploads);

            upTimestamp="unknown";
            upComment="unknown";
            upFileName="unknown";
            upSrc="unknown";
            upSize="unknown";

            currentState=XMLElementState.Page;
            currentProperty=XMLElementProperties.defaultP;



            contribId="unknown";
            contribIp="unknown";
            contribUserName="unknown";

            contribIdB.setLength(0);
            contribIpB.setLength(0);
            contribUsernameB.setLength(0);
        }
        else if (qName.equalsIgnoreCase("contributor")) {
            contributors.clear();


            if(contribIdB.length()!=0 && !contribIdB.toString().startsWith("\n")) {
                contribId = contribIdB.toString();
            }
            contributors.add(contribId);
            // when it's <username></username> contribUsernameB is "\n       " ?!

            int l = contribUsernameB.length();
            if(contribUsernameB.length()!=0 && !contribUsernameB.toString().startsWith("\n")) {
               contribUserName = cleanString(contribUsernameB.toString());
            }
            contributors.add(contribUserName);
            if(contribIpB.length()!=0 && !contribIpB.toString().startsWith("\n")) {
                contribIp = contribIpB.toString();
            }
            contributors.add(contribIp);

            printWithGuava(contributorsFile,contributors);


            contribUserName="unknown";
            contribUsernameB.setLength(0);

            currentState=XMLElementState.Revision;
            currentProperty=XMLElementProperties.defaultP;

        }
        else if (qName.equalsIgnoreCase("mediawiki")) {
        // end of document
            if( listWriterP != null ) {
                try {
                    listWriterP.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( listWriterR != null ) {
                try {
                    listWriterR.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( listWriterC != null ) {
                try {
                    listWriterC.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

            //found characters to process


        switch(currentProperty) {
            case pageTitleP:
                pageTitleB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case pageNsP:
                pageNsB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                // pageNs = new String(ch, start, length);

                break;
            case pageIdP:
                pageIdB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                // pageId = new String(ch, start, length);

                break;
           /* case pageRedirectP:
                pageRedirectsB.append(new String(ch, start, length));
                break;
            case pageRestrictionsP:
                pageRestrictionsB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case pageDiscussionP:
                pageDiscussion = new String(ch, start, length);
                currentProperty= XMLElementProperties.defaultP;
                break;*/
            case revIdP:
                revIdB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                //revId = new String(ch, start, length);

                break;
            case revParentIdP:
                revParentIdB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                //revParentId= new String(ch, start, length);

                break;
            case revTimestampP:
                revTimestampB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                // revTimestamp=new String(ch, start, length);

                break;
            case revMinorP:
                revMinorB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                //revMinor=new String(ch, start, length);

                break;
            case revCommentP:
                revCommB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                //String escHtmlCom = StringEscapeUtils.unescapeHtml(comm);
                //String escXmlComm = StringEscapeUtils.unescapeXml(escHtmlCom);
               // revComment= StringEscapeUtils.escapeCsv(comm);
                break;
            case revTextP:
               /* String text=new String(ch, start, length);
                revText = StringEscapeUtils.escapeCsv(text);*/

                break;
            case revSha1P:
                revSha1B.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case revModelP:
                revModelB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case revFormatP:
                revFormatB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case upTimestampP:
                //TODO when considering uploads this field also needs to get into a StringBuilder
                upTimestamp=new String(ch, start, length);

                break;
            case upCommentP:
                //String commup = new String(ch, start, length);
                //upComment=StringEscapeUtils.escapeCsv(commup);
                upCommB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case upFileNameP:
                upFileNameB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));

                break;
            case upSrcP:
                upSrc=new String(ch, start, length);

                break;
            case upSizeP:
                upSize=new String(ch, start, length);

                break;
            case contribUserNameP:
                contribUsernameB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                break;
            case contribIdP:
                contribIdB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                // contribId=new String(ch, start, length);

                break;
            case contribIpP:
                contribIpB.append(StringEscapeUtils.unescapeXml(new String(ch, start, length)));
                //contribIp=new String(ch, start, length);

                break;
        }


    }

    private String cleanString(String inputString)
    {
        String normalized = StringUtils.normalizeSpace(inputString);
        String result = new String(normalized);
        if(result.contains("\\n"))
        {
            result = normalized.replace("\\n", "");
        }

        if(result.contains("\\t"))
        {
            result = normalized.replace("\\t", " ");
        }

        if(result.contains("\\"))
        {
            result = normalized.replace("\\", "");
        }



        return result;
    }




}
