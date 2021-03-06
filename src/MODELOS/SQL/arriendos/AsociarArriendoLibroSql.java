/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MODELOS.SQL.arriendos;

import MODELOS.CONFIG_SQL.ConecctSql;
import MODELOS.MODELOCLASES.libros.Libro;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Daniel Huenul
 */
public class AsociarArriendoLibroSql extends ConecctSql {

    private PreparedStatement ps;
    private ResultSet rs;
    
    public boolean insert(int idarri, int idlib) {
        Connection con = null;
        try {
            String sql = "INSERT INTO `libros_en_arriendo`(`idLibros_en_Arriendo`, `Arriendo_idArriendo`, `Libros_idLibro`) "
                    + "VALUES (0,?,?)";
            con = getConection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idarri);
            ps.setInt(2, idlib);
             
            if(ps.executeUpdate() != 0){
                System.err.println("AGREGADO.....");
                String sqlUpdate = "UPDATE `libros` SET Estados_idEstado = 15 WHERE idLibro = "+idlib+"";
                PreparedStatement mips = con.prepareStatement(sqlUpdate);
                if(mips.executeUpdate() != 0){
                    System.err.println("ESTADO CAMBIADO.....");
                }else{
                    System.err.println("ESTADO NO CAMBIADO.....");
                }
                
            }else{ 
                System.err.println("NO AGREGADO.....");
            }
            return true;
        } catch (Exception e) {
            System.err.println("error CATCH insert:" + e);
            return false;
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error finally insert:" + e);
            }
        }
    }

    public int getCantidad(int id) {
        Connection con = getConection();
        int numero = 0;
        try {

            String sql = "SELECT COUNT(Arriendo_idArriendo) FROM libros_en_arriendo WHERE Arriendo_idArriendo = ?";
            con = getConection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();
            while (rs.next()) {
                numero = rs.getInt("COUNT(Arriendo_idArriendo)");
            }
            return numero;
        } catch (Exception e) {
            System.err.println("error: CATCH getCantidad: " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error: Finally getCantidad" + e);
            }
        }

        return numero;
    }

    public int[] idLibrosSinAñadir(int idArri, int cantidadRegistros) {
        Connection con = getConection();
        int listadoIds[] = new int[cantidadRegistros];

        try {

            String sql = "SELECT Libros_idLibro FROM libros_en_arriendo WHERE Arriendo_idArriendo = ?";
            con = getConection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArri);

            rs = ps.executeQuery();
            int cont = 0;
            while (rs.next()) {

                listadoIds[cont] = rs.getInt("Libros_idLibro");

                cont++;
            }

        } catch (Exception e) {
            System.err.println("error: CATCH idCorreosSinAñadir: " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error: Finally idCorreosSinAñadir" + e);
            }
        }

        return listadoIds;
    }

    public ArrayList<Libro> milistadoLibros(int[] listado, boolean igualdad, int id) {

        Connection con = getConection();
        ArrayList<Libro> lista = new ArrayList<>();
        Libro obj;

        try {
            //idCorreo	correo	contraseña 
            con = getConection();
            String sql = "SELECT * FROM libros ";

            //si igualdad es true: se tienen que buscar las diferencias de ID
            //si iigualdad es false: se deben buscar las diferencias
            //para que solo muestre resultados que esten en bodega SELECT * FROM libros WHERE idLibro != 26 AND Estados_idEstado = 17
            if (igualdad) {
                if (listado.length != 0) {
                    for (int i = 0; i < listado.length; i++) {
                        if (i == 0) {
                            sql += "WHERE idLibro != " + listado[i] + " ";
                        } else {
                            sql += "AND idLibro != " + listado[i] + " ";
                        }

                    }   
                    sql += "AND Estados_idEstado = 17";
                }else{
                    sql += "WHERE Estados_idEstado = 17";
                }
                System.out.println("SQL TRUE: " + sql);
            } else {
                sql = "SELECT lib.idLibro, lib.isbn, lib.num_serie, lib.nombre FROM libros_en_arriendo libarri INNER JOIN libros lib ON libarri.Libros_idLibro = lib.idLibro INNER JOIN arriendos arri ON libarri.Arriendo_idArriendo = arri.idArriendo WHERE Arriendo_idArriendo = "+id+"";
                System.out.println("SQL FALSE: " + sql);

            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                //idLibro	isbn	num_serie	nombre	idEstado	estado	idEditorial	nombre	 
                obj = new Libro();
                obj.setId(rs.getInt("idLibro"));
                obj.setIsbn(rs.getInt("isbn"));
                obj.setNumSerie(rs.getInt("num_serie"));
                obj.setNombre(rs.getString("nombre"));
                lista.add(obj);
            }

        } catch (Exception e) {
            System.err.println("error: CATCH milistadoLibros: " + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error: Finally milistadoLibros" + e);
            }
        }

        return lista;
    }

    public boolean VerificarLib(Libro obj) {
        Connection con = null;
        boolean est = true;
        try {

            String sql = "SELECT * FROM libros WHERE idLibro = ?";
            con = getConection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, obj.getId());
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("id -> "+ rs.getInt("idLibro"));
                obj.setId(rs.getInt("idLibro")); 
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("error CATCH VerificarLib:" + e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error finally VerificarLib:" + e);
            }
        }
        return false;
    }

    public boolean delete(int idc, int idl) {
        Connection con = null;
        try {
            String sql = "DELETE FROM libros_en_arriendo WHERE Libros_idLibro = "+idl+" AND Arriendo_idArriendo = "+idc+"";
            con = getConection();
            ps = con.prepareStatement(sql); 

            if(ps.executeUpdate() != 0){
                System.err.println("ELIMINADO.....");
                String sqlUpdate = "UPDATE `libros` SET Estados_idEstado = 17 WHERE idLibro = "+idl+"";
                PreparedStatement mips = con.prepareStatement(sqlUpdate);
                if(mips.executeUpdate() != 0){
                    System.err.println("ESTADO CAMBIADO.....");
                }else{
                    System.err.println("ESTADO NO CAMBIADO.....");
                }
                
            }else{ 
                System.err.println("NO ELIMINADO.....");
            }
            return true;
        } catch (Exception e) {
            System.err.println("error CATCH delete:" + e);
            return false;
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                System.err.println("error finally delete:" + e);
            }
        }
    }
}
