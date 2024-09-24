import org.apache.log4j.Logger;

import java.awt.desktop.PreferencesEvent;
import java.io.PrintWriter;
import java.sql.*;

public class ClinicaOdontologica {

    // Las columnas deben conincidir con el objeto que estamos modelando
    private static final String SQL_CREATE_TABLE = "DROP TABLE IF EXISTS DENTIST; "
            + "CREATE TABLE DENTIST "
            + "("
            + " ID INT PRIMARY KEY,"
            + " REGISTRATION INT NOT NULL, "
            + " NAME varchar(100) NOT NULL, "
            + " LASTNAME varchar(100) NOT NULL"
            +" )";

    private static final String SQL_INSERT = "INSERT INTO DENTIST (ID, REGISTRATION, NAME, LASTNAME)" +
            " VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT = "SELECT * FROM DENTIST";

    private static final String SQL_UPDATE = "UPDATE DENTIST SET NAME=? WHERE ID=?";

    private static final  String SQL_SELECT_ID = "SELECT * FROM DENTIST WHERE ID=?";

    private static final String SQL_DELETE_ID = "DELETE FROM DENTIST WHERE ID=?";

    private static final Logger LOGGER = Logger.getLogger(ClinicaOdontologica.class);


    public static void main(String[] args) throws SQLException {

        Dentist dentist1 = new Dentist(1, 3442, "Ilsen", "Sanchez");
        Dentist dentist2 = new Dentist(2, 4355, "Llud", "Sanchez");

        Connection connection = null;

        try {
            connection = getConnection();

            // crear la tabla y definir sus columnas en la BD
            Statement statement = connection.createStatement();
            statement.execute(SQL_CREATE_TABLE);

            // insertar valores en la BD
            PreparedStatement psInsert = connection.prepareStatement(SQL_INSERT);

            psInsert.setInt(1, dentist1.getId());
            psInsert.setInt(2, dentist1.getRegistration());
            psInsert.setString(3, dentist1.getName());
            psInsert.setString(4, dentist1.getLastName());
            psInsert.execute();

            psInsert.setInt(1, dentist2.getId());
            psInsert.setInt(2, dentist2.getRegistration());
            psInsert.setString(3, dentist2.getName());
            psInsert.setString(4, dentist2.getLastName());
            psInsert.execute();

            // consultar la carga de los datos en la tabla
            ResultSet rs = statement.executeQuery(SQL_SELECT);

            int n = 1;

            while (rs.next()) {
                System.out.println("Los valores insertados en la tabla son los siguientes: " +
                        " En la fila " + n +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
                LOGGER.info("Los valores insertados en la tabla son los siguientes: " +
                        " En la fila " + n +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
                // n = n + 1;
                n += 1;
            }

            // actualizar algún dato de una de las filas (de unos de los objetos) en la BD
            // transaccion
            connection.setAutoCommit(false);

            PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE);
            String update = "Genrry";
            psUpdate.setString(1, update);
            psUpdate.setInt(2, dentist1.getId());
            psUpdate.execute();

            connection.commit();
            LOGGER.warn("SE ACTUALIZO EN EL REGISTRO CORRESPONDIENTE AL ID: " + dentist1.getId());
            connection.setAutoCommit(true);

            // Borrar un registro de la BD
            // Transaccion
            connection.setAutoCommit(false);

            PreparedStatement psDelete = connection.prepareStatement(SQL_DELETE_ID);
            psDelete.setInt(1,dentist2.getId());
            psDelete.execute();

            connection.commit();
            LOGGER.warn("AVISO: se elimino el registro con id 2");
            connection.setAutoCommit(true);


        } catch (Exception e) {
            connection.rollback();
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        } finally {
            connection.close();
        }
        // imprimir por consola el valor que modificamos
        try {
            connection = getConnection();

            int id = 1;

            PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ID);
            ps.setInt(1,id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Los datos actualizados: " +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
                LOGGER.info("Los datos actualizados: " +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

        // imprimir/chequear por consola que se borró el registro con el ID=2 de nuestra tabla en la BD
        try {
            connection = getConnection();

            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(SQL_SELECT);

            while (rs.next()) {
                System.out.println("La consulta final luego de borrar el registro con el ID 1 es: " +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
                LOGGER.info("La consulta final luego de borrar el registro con el ID 1 es: " +
                        " ID: " + rs.getInt(1) + " Matricula: " + rs.getInt(2) + " Nombre: " +
                        rs.getString(3) + " Apellido: " + rs.getString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

    }

    private static Connection getConnection () throws  Exception{
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:~/ClinicaOdontologica");
    }
}
