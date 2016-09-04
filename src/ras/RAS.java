/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ras;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import static ras.form.RASMain.vpg;
import ras.vlc.VLCPlaylistGenerator;

/**
 *
 * @author Rafael
 * Version 1.2 - SNAPSHOT - RETIRANDO AS RESPONSABILIDADES DE RASMain PARA A VERSÃO 1.3
 */
public class RAS {

    /**
     * 
     * @param vpg
     * @param file
     * @param txtFolder
     * @param tblLista
     * @param tbDetail 
     */
    
    public void randomSelect(VLCPlaylistGenerator vpg, File file, JTextField txtFolder, JTable tblLista, JTable tbDetail){
        int filesSize;
        int selectThis;
        Double nRand;
        Random rands;
        boolean assistir = false;
        int opcao;
        File[] files;
        if(file == null){
            if(txtFolder.getText() != null && !txtFolder.getText().isEmpty())
                JOptionPane.showMessageDialog(null, "Clique em Load Folder");
            else
                JOptionPane.showMessageDialog(null, "Selecione um folder primeiro");
            return;
        }
        if(vpg != null)
            try{
               vpg.deletePlaylists("./");
               vpg = new VLCPlaylistGenerator();
            }catch(Exception e){
               e.printStackTrace();
        }else{
            vpg = new VLCPlaylistGenerator();
        }
        
        filesSize = file.list().length;
        files = file.listFiles();
        do{
                random: do{
//                nRand = Math.random()*filesSize;
//                selectThis = nRand.intValue();
                  rands = new Random();
                  selectThis = rands.nextInt(filesSize);
        }while(!files[selectThis].isDirectory());     

        opcao = JOptionPane.showConfirmDialog(null,"Deseja asssistir " + files[selectThis].getName() + "?"); 
            if(opcao == JOptionPane.YES_OPTION){
                try{                    
                    try{
                        vpg.generatePlaylist(files[selectThis].getPath(),files[selectThis].getName());
                    }catch(Exception e){
                        e.printStackTrace();
                        return;
                    }                    
                    java.awt.Desktop.getDesktop().open(new File(vpg.getPlayListPath()));     
                    tbDetail.setRowSelectionInterval(selectThis, selectThis);
                    tblLista.setRowSelectionInterval(selectThis, selectThis);
                    
                    assistir = true;
                }catch(IOException e){
                    JOptionPane.showMessageDialog(null, "Erro ao abrir folder");
                    e.printStackTrace();
                }

            }else if(opcao == JOptionPane.NO_OPTION){
                assistir = false;
            }else{
                assistir = true;
            }
        }while(!assistir);        
    }
    
    /**
     * 
     * @param dtm
     * @param detModel
     * @param txtFolder
     * @param file
     * @param tbDetail
     * @param tblLista 
     */
    public void loadFolder(DefaultTableModel dtm,DefaultTableModel detModel,JTextField txtFolder,
            File file, JTable tbDetail, JTable tblLista, JLabel lblQtdResultados ){
        dtm.setRowCount(0);
        detModel.setRowCount(0);
        if(txtFolder.getText() == null
                || txtFolder.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Selecione um diretório");
            detModel.addRow(new Object[]{"Diretório Vazio"});
            dtm.addRow(new Object[]{"Diretório Vazio"});
            return;
        }
       
        File f = new File(txtFolder.getText());
        if(!f.canExecute()){
            JOptionPane.showMessageDialog(null, "Selecione um diretório válido");
            detModel.addRow(new Object[]{"Diretório Vazio"});
            dtm.addRow(new Object[]{"Diretório Vazio"});
            return;
        }
        file = f;
        File[] ls;
        ls = f.listFiles();
        
        Object[] normalRow;
        Object[] detRow;
        int dirCount = 0;
        int fileCount = 0;

        for(File fs: ls){
            if(fs.isDirectory()){
                dirCount = 0;
                fileCount = 0;
                File[] auxList;
                auxList = fs.listFiles();
                if(auxList != null)
                    for(File aux: auxList){
                        if(aux.getAbsolutePath().contains("Thumbs"))
                            continue;
                        if(aux.isDirectory())
                            dirCount++;
                        else
                            fileCount++;
                    }
//                Object[] obj = new Object[]{fs.getName(),fs.list().length, 0, 0};
                normalRow = new Object[]{fs.getName(),fs.getAbsolutePath()};
                detRow = new Object[]{fs.getName(),fileCount,dirCount,fs.getAbsolutePath()};
                }else{
                   normalRow = new Object[]{fs.getName(),fs.getAbsolutePath()};
                   detRow = new Object[]{fs.getName(),0,0,fs.getAbsolutePath()};
                
                }
                   dtm.addRow(normalRow);         
                   detModel.addRow(detRow);
        }
        if(tbDetail.getRowCount() <= 0){
            detRow = new Object[]{"Diretório Vazio"};
            detModel.addRow(detRow);
        }
        if(tblLista.getRowCount() <= 0){
            normalRow = new Object[]{"Diretório Vazio"};
            dtm.addRow(normalRow);
        }
        lblQtdResultados.setText(String.valueOf(ls.length));
        tbDetail.setShowGrid(true);
        tblLista.setShowGrid(true);
    }
    
    /**
     * 
     * @param dtm
     * @param tblLista
     * @param tbDetail 
     */
    public void getFolderDetail(DefaultTableModel dtm, JTable tblLista, JTable tbDetail){
        StringBuilder sb = new StringBuilder();
        File f;
        int row;
        if(tblLista.getSelectedRowCount()<= 0 || !tblLista.isFocusOwner()){
            if(tbDetail.getSelectedRowCount() <= 0 || !tbDetail.isFocusOwner()){
                        JOptionPane.showMessageDialog(null,"Selecione uma linha!");
                        return;
            }
            row = tbDetail.getSelectedRow();
        }else{
            row = tblLista.getSelectedRow();
        }
        
        f = new File(dtm.getValueAt(row, 1).toString());
        int folderSize = f.listFiles() == null ? 0 : f.listFiles().length;
        
        sb.append("Nome: ").append(dtm.getValueAt(row, 0));            
        sb.append("\nPath: ").append(dtm.getValueAt(row, 1));            
        sb.append("\nItens: ").append(folderSize);
        
        int i = 0;
        int j = 0;
        if(folderSize > 0){
            File[] fs = f.listFiles();
            for(File ax: fs){
                if(ax.isDirectory())
                    j++;
                else
                    i++;
            }
            sb.append("\nDiretórios na pasta: ").append(j);
            sb.append("\nQtde Arquivos na pasta: ").append(i);
            
        }
        JOptionPane.showMessageDialog(null, sb.toString());        
    }
    
    /**
     * 
     * @param tblLista
     * @param tbDetail 
     */
    public void openFolder(JTable tblLista, JTable tbDetail){
         String path;
        File f;
        if(tblLista.getSelectedRowCount() <=0 || !tblLista.isFocusOwner()){
            if(tbDetail.getSelectedRowCount() <= 0){
                JOptionPane.showMessageDialog(null,"Selecione uma linha!");
                return;
            }
            path = tbDetail.getValueAt(tbDetail.getSelectedRow(),3).toString();
            f = new File(path);
        }else{
            path = tblLista.getValueAt(tblLista.getSelectedRow(),1).toString();

            f = new File(path);
        }
        try{
            java.awt.Desktop.getDesktop().open(f);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
    
    /**
     * 
     * @param txtFolder ,
     */
    public void folderSelector(JTextField txtFolder){
            JFileChooser jfc = new JFileChooser();
        
        if(!txtFolder.getText().isEmpty())
            jfc.setCurrentDirectory(new java.io.File(txtFolder.getText()));
        else{
            jfc.setCurrentDirectory(new java.io.File("C:/"));
        }
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            try{
                txtFolder.setText(jfc.getSelectedFile().getCanonicalPath());
            }catch(Exception e){
                e.printStackTrace();
            }
        }          
    }
        
}
