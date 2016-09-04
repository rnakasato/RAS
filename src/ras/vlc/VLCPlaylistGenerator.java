/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ras.vlc;

import java.io.File;
import java.io.FileWriter;

/**Classe a ser refatorada e melhorada, não utilizar para novos projetos até cumprir os seguintes requisitos:
 * Melhorar organização da classe, definir os métodos de construção de xml como private, de forma que apenas os 
 * métodos que manipulem o xml utilizem esses métodos
 * 
 * Criar métodos para gerar playlists verificando no folder as extensões dos arquivos dentro deles, para não gerar 
 * playlists para formatos que não sejam vídeo ou mp3, verificar a possibilidade de salvar as extensões em um arquivo
 * ou gera banco em SQLite para registrar os formatos aceitos na geração da playlist
 * 
 * 
 * Criar ajuda informando as extensões aceitas para a geração da playlist
 * 
 * Verificar se tem como gerar playlist para Winamp, a estrutura das playlists no Winamp são definidas em um arquivo que indica a 
 * playlist e faz referência a um arquivo contendo todos os arquivos lidos na playlist
 * 
 * Desenvolver ou procurar algoritmo de busca dentro de folders
 * 
 * Desenvolver cache em arquivo contendo pastas onde já foram geradas as playlists para agilizar a geração
 *  - Verificar como utilizar o cache de forma rápida, considerando que no folder que 
 * já foi gerado a playlist o conteúdo pode ter sido alterado
 * 
 * 
 * 
 * @author Rafael
 */


public class VLCPlaylistGenerator {
    private String playListPath;
    
    
   /**
    * Gera playlist na pasta do executável (Remanejar para diretório de playlists)
    *@param fileLocation - Caminho do arquivo/folder de origem para a playlist
    *@param playListName - Nome para a playlist a ser gerada
    * 
    * 
    */
    public void generatePlaylist(String fileLocation, String playlistName) throws Exception{
        String xmlBody;
        File f;
        try{
            f = new File("./"+playlistName + ".xspf");
              
        FileWriter fw = new FileWriter(f);
        StringBuilder sb = new StringBuilder();
        sb.append(openPlayListXML());
        sb.append(openTrackXML());
        sb.append(getTrackLocationXML(fileLocation));
        sb.append(closeTrackXML());
        sb.append(closePlayListXML());
        xmlBody = sb.toString();
        fw.write(xmlBody);  
        fw.close();
        setPlayListPath(f.getAbsolutePath());
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Erro ao criar playlist");
        }  
    }
    public void deletePlaylists(String fileLocation) throws Exception{
        try{
            File f = new File(fileLocation);
            File[] files = f.listFiles();
            for(File fs: files){
                if(fs.getAbsolutePath().contains(".xspf")){
                    fs.delete();
                }
            }
            f.delete();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Erro ao deletar playlist");
        }        
    }
    
    public void deletePlayList(String fileLocation) throws Exception{
        try{
            File f = new File(fileLocation);

            if(f.getAbsolutePath().contains(".xspf")){
                   f.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Erro ao deletar playlist");
        }        
    }
    public File getLastPlaylist(String fileLocation){
        File f = new File(fileLocation);
        if(f.list() != null){
            File[] files = f.listFiles();
            for(File fs: files){
                if(fs.getAbsolutePath().contains(".xspf")){
                    return fs;
                }
            }
        }
       return null;
    }
    
    private String getTrackLocationXML(String fileLocation){
        String formatter;
        formatter = fileLocation.replaceAll(" ", "%20");
        formatter = formatter.replaceAll("'\'","/");
        
        StringBuilder sb = new StringBuilder();
        sb.append("<location>file:///");
        sb.append(formatter);
        sb.append("</location>\n");
        return sb.toString();
    }                    
    private String openTrackXML(){
        return "<track>\n";
    }
    private String closeTrackXML(){
        return "<extension application=\"http://www.videolan.org/vlc/playlist/0\">\n" +
                        "				<vlc:id>0</vlc:id>\n" +
                        "			</extension>\n" +
                        "		</track>\n";
    }
            
    
    
    private String openPlayListXML(){
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<playlist xmlns=\"http://xspf.org/ns/0/\" xmlns:vlc=\"http://www.videolan.org/vlc/playlist/ns/0/\" version=\"1\">\n"+
                "<title>Lista de reproducao</title>\n"+
                "<trackList>\n";
                    
    }
    private String closePlayListXML(){
        return  "</trackList>\n" +
                "	<extension application=\"http://www.videolan.org/vlc/playlist/0\">\n" +
                        "			<vlc:item tid=\"0\"/>\n" +
                        "	</extension>\n" +
                        "</playlist>";
    }

    /**
     * @return the playListPath
     */
    public String getPlayListPath() {
        return playListPath;
    }

    /**
     * @param playListPath the playListPath to set
     */
    public void setPlayListPath(String playListPath) {
        this.playListPath = playListPath;
    }
}
