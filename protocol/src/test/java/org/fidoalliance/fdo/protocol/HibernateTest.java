package org.fidoalliance.fdo.protocol;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.commons.codec.DecoderException;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;


public class HibernateTest {

  private String dbName = "test"; //will be test.mv.db
  private static final int DEFAULT_PORT = 9092;

  private void test(String className, Session session) {

    Class stgClass = null;
    try {
      stgClass = Class.forName(className);
      CriteriaBuilder cb = session.getCriteriaBuilder();
      CriteriaQuery<?> cr = cb.createQuery(stgClass);
      //session.createQuery(stgClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();

    }
  }



  @Test
  public void Test() throws DecoderException, IOException {
    /*
    System.out.println(System.getProperty("user.dir"));
    List<String> tcpParams = new ArrayList<>();
    tcpParams.add("-ifNotExists");
    //tcpParams.add("-baseDir");



    String[] tcpArgs = new String[tcpParams.size()];
    tcpParams.toArray(tcpArgs);
    int port = DEFAULT_PORT;
    //Server server = null;

    System.setProperty("derby.system.home", "C:\\Users\\RFTEMPLE\\OneDrive - Intel Corporation\\Documents\\cri\\demos\\spike\\src\\main\\webapp\\config");

    // variables
    //String dbURL = "jdbc:derby:testdb;create=true;user=me;password=mine";
    String dbURL = "jdbc:derby:testdb";

    Connection con = null;

    //con = DriverManager.getConnection(dbURL);
    //con.close();
    hibernate();


      Class.forName("org.h2.Driver");

      server = Server.createTcpServer(tcpArgs);
      server.start();
      port = server.getPort();
      boolean running = server.isRunning(true);
      if (running) {

        List<String> webParams = new ArrayList<>();

        webParams.add("-web");
        webParams.add("-webAllowOthers");
        webParams.add("-webPort");
        webParams.add("8082");

        String[] webArgs = new String[webParams.size()];
        webArgs = webParams.toArray(webArgs);
        Server web = Server.createWebServer(webArgs);
        web.start();
        int webPort = web.getPort();
        if (web.isRunning(true)) {
          System.out.println("Web server running at " + webPort);
          String connectionString = "jdbc:h2:tcp://localhost:"
              + port + "/./" + dbName;

          System.out.println(connectionString);
          hibernate();
          Thread.currentThread().join();
          HibernateUtil.shutdown();

        }

      }*/

  }


}