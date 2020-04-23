/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pdfextractor;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author etomi
 */
public class pdfExtractor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        // TODO code application logic here

        try (PDDocument document = PDDocument.load(new File("C:\\\\pdf.pdf"))) {

            document.getClass();

            if (!document.isEncrypted()) {
			
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();

                String pdfFileInText = tStripper.getText(document);
                //System.out.println("Text:" + st);
                
                int topic = 0;
                int question = 0;
                int contadorRespuestas = 0;
                boolean marcaPregunta = false;
                boolean marcaRespuesta = false;
                boolean marcaCorrecta = false;
                String pregunta = "";
                String respuestaA = "";
                String respuestaB = "";
                String respuestaC = "";
                String respuestaD = "";
                String respuestaE = "";
                String opcionCorrecta = "";
                String correcta = "";
                
                String lines[] = pdfFileInText.split("\\r?\\n");         
                
                for (String line : lines) {

                    if (!line.contains("16/3/2020") && !line.contains("AWS-SysOps Exam") && !line.contains("/262")) {
                    
                        if (line.contains("Question #"))
                        {
                            if(opcionCorrecta != ""){
                                System.out.println("pregunta: " + pregunta.substring(7));
                                System.out.println("topic: " + pregunta.substring(0, 7));
                                System.out.println("respuestA: " + respuestaA);
                                System.out.println("respuestB: " + respuestaB);
                                System.out.println("respuestC: " + respuestaC);
                                System.out.println("respuestD: " + respuestaD);
                                System.out.println("respuestE: " + respuestaE);
                                System.out.println("opcionCorrecta: " + opcionCorrecta);
                                System.out.println("correcta: " + correcta);
                                System.out.println("question: " + question);
                                
                                int index = 0;
                                String regex = "[0-9]+";
                                String preguntaSinTopic =  pregunta.substring(7);
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(preguntaSinTopic);
                                
                                while (matcher.find()) {
                                    index = matcher.end();
                                   /* System.out.print("Start index: " + matcher.start());
                                    System.out.print(" End index: " + matcher.end());
                                    System.out.println(" Found: " + matcher.group());*/
                                    break;
                                }
                                /*System.out.println(" Substring Pregunta: " + preguntaSinTopic);
                                System.out.println(" Substring Pregunta: " + preguntaSinTopic.substring(index));
                                System.out.println("matcher start: " + index);*/
                                
                                insertBD(preguntaSinTopic.substring(index), pregunta.substring(0, 7), respuestaA, respuestaB, respuestaC, respuestaD, respuestaE, opcionCorrecta, correcta, question);
                            }                        
                            pregunta = "";
                            respuestaA = "";
                            respuestaB = "";
                            respuestaC = "";
                            respuestaD = "";
                            respuestaE = "";
                            opcionCorrecta = "";
                            correcta = "";
                            //topic = Integer.valueOf(line.substring(6, 7));
                            System.out.println("line Question: " + line);
                            if(line.substring(16,17).equals("#")){
                                System.out.println("line sub1: " + line.substring(17));
                                question = Integer.valueOf(line.substring(17));
                            } else {
                                System.out.println("line sub2: " + line.substring(line.indexOf("#")));
                                question = Integer.valueOf(line.substring(line.indexOf("#")+1));
                            }
                            marcaPregunta = true;
                            marcaCorrecta = false;
                        }
                        if (marcaPregunta){

                            if (line.startsWith("A.")){
                                marcaPregunta = false;
                                marcaRespuesta = true;
                            } else {
                                pregunta = pregunta + line;
                            }
                        }

                        if (marcaRespuesta){

                            switch (contadorRespuestas) {
                                case 0:

                                    if(line.contains("B.")){
                                        contadorRespuestas++;
                                        respuestaB = respuestaB + line;
                                        break;
                                    }   
                                    respuestaA = respuestaA + line;
                                    break;
                                case 1:
                                    if(line.contains("C.")){
                                        contadorRespuestas++;
                                        respuestaC = respuestaC + line;
                                        break;
                                    }   
                                    respuestaB = respuestaB + line;
                                    break;
                                case 2:

                                    if(line.contains("D.")){
                                        contadorRespuestas++;
                                        respuestaD = respuestaD + line;
                                        break;
                                    }   
                                    respuestaC = respuestaC + line;
                                    break;
                                case 3:
                                    if(line.contains("E.")){
                                        contadorRespuestas++;
                                        respuestaE = respuestaE + line;
                                        break;
                                    }   

                                    if(line.contains("Correct Answer")){
                                        contadorRespuestas=0;
                                        marcaRespuesta = false;
                                        marcaCorrecta = true;
                                        opcionCorrecta = line.substring(16, 17);
                                        break;
                                    } else if(line.contains("Correct Answer")) {

                                    } 
                                    respuestaD = respuestaD + line;
                                    break;
                                case 4:
                                    if(line.contains("Correct Answer")){
                                        contadorRespuestas=0;
                                        marcaRespuesta = false;
                                        marcaCorrecta = true;
                                        opcionCorrecta = line.substring(16);
                                        break;
                                    }   
                                    break;   
                                default:
                                    break;
                            }
                    }
                    
                    if (marcaCorrecta) {
                        correcta = correcta + line;
                        marcaCorrecta = false;
                    }
                   
                    

                } 
                
                /*for (String line : lines) {
                    System.out.println(line);
                }*/
                       }
            }
        }
         
    }
    
    static public void insertBD (String pregunta, String topic, String respuestaA, String respuestaB, String respuestaC, String respuestaD, String respuestaE, String opcionCorrecta, String correcta, int question) throws SQLException{
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/awssysops?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "admin", "Pa$$w0rd123");
            System.out.println("SQL Connection to database established!");
 
              String query = " insert into preguntas (pregunta, topic, respuestaA, respuestaB, respuestaC, respuestaD, respuestaE, opcionCorrecta, correcta, numeroPregunta)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString (1, pregunta);
            preparedStmt.setString (2, topic);
            preparedStmt.setString (3, respuestaA);
            preparedStmt.setString (4, respuestaB);
            preparedStmt.setString (5, respuestaC);
            preparedStmt.setString (6, respuestaD);
            preparedStmt.setString (7, respuestaE);
            preparedStmt.setString (8, opcionCorrecta);
            preparedStmt.setString (9, correcta);
            preparedStmt.setInt (10, question);
       
    // execute the preparedstatement
          preparedStmt.execute();
          connection.close();
    }
}
