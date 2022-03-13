package TempOutput;

import entity.BaseEntity;
import entity.FileEntity;
import entity.PackageEntity;
import util.SingleCollect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependsString {

    public static class LocationDTO {
        int line;
        int row;

        public LocationDTO(int line, int row){
            this.line = line;
            this.row = row;
        }
    }

    public static class DetailDTO {
        int fromEntity;
        int toEntity;
        String relationType;
        LocationDTO location;

        public DetailDTO(int fromEntity, int toEntity, String relationType, int line, int row){
            this.fromEntity = fromEntity;
            this.toEntity = toEntity;
            this.relationType = relationType;
            this.location = new LocationDTO(line, row);
        }
    }

    public static class CellsDTO {
        int srcFile;
        int destFile;
        HashMap<String, Integer> values = new HashMap<>();
        ArrayList<DetailDTO> details = new ArrayList<>();

        public CellsDTO(int srcFile, int destFile){
            this.srcFile = srcFile;
            this.destFile = destFile;
        }

        public void addValue(String relationType){
            if (this.values.containsKey(relationType)){
                int count = this.values.get(relationType) + 1;
                this.values.replace(relationType, count);
            } else {
                this.values.put(relationType, 1);
            }
        }
    }

    public static class IndicesDTO {

        String object;
        String file;
        LocationDTO location;
        String type;
        String rawType;

        public IndicesDTO(String object, String file, int line, int row, String type, String rawType){
            this.object = object;
            this.file = file;
            this.location = new LocationDTO(line, row);
            this.type = type;
            this.rawType = rawType;
        }
    }

    String name;
    String lang;
    String rootDir;
    Integer nodeNum;
    Integer edgeNum;
    ArrayList<CellsDTO> cells = new ArrayList<>();
    ArrayList<String> variables = new ArrayList<>();
    ArrayList<IndicesDTO> indices = new ArrayList<>();
    Integer indexNum;

    protected SingleCollect singleCollect = SingleCollect.getSingleCollectInstance();

    public DependsString(){

    }

    public DependsString(String name, String lang, String rootDir){
        this.name = name;
        this.lang = lang;
        this.rootDir = rootDir;
    }

    public void addCell(CellsDTO cell){
        this.cells.add(cell);
    }

    public void addVariables(String filePath){
        this.variables.add(filePath);
    }

    public void addIndice(IndicesDTO indice){
        this.indices.add(indice);
    }

    /**
     * get current entity's file id
     * @param id
     * @return
     */
    public int getCurrentFileId(int id){
        if(singleCollect.getEntityById(id) instanceof FileEntity){
            return id;
        }
        else if (singleCollect.getEntityById(singleCollect.getEntityById(id).getParentId()) instanceof FileEntity){
            return singleCollect.getEntityById(id).getParentId();
        }
        else {
            return getCurrentFileId(singleCollect.getEntityById(id).getParentId());
        }
    }

    public DependsString getDependsString(String projectName, String projectPath, String lang){
        DependsString dependsString = new DependsString(projectName, lang, projectPath);

        for (BaseEntity entity : singleCollect.getEntities()){
            if (entity instanceof PackageEntity){
                continue;
            } else {
                String entityFile = ((FileEntity)singleCollect.getEntityById(getCurrentFileId(entity.getId()))).getFullPath();
                IndicesDTO indice = new IndicesDTO(entity.getQualifiedName(), entityFile, entity.getLocation().getStartLine(),
                        entity.getLocation().getStartColumn(), singleCollect.getEntityType(entity.getId()), null);
                dependsString.addIndice(indice);
            }
        }
        dependsString.indexNum = singleCollect.getEntities().size();

        for (Integer fileId : singleCollect.getFileIds()){
            dependsString.addVariables(((FileEntity)singleCollect.getEntityById(fileId)).getFullPath());
        }

        JsonMap jsonMap = new JsonMap();
        Map<Integer, Map<Integer, Map<String, Integer>>> relationMap = jsonMap.getFinalRes();
        for(int fromEntity:relationMap.keySet()) {
            int src = singleCollect.getFileIndex(getCurrentFileId(fromEntity));
            for (int toEntity : relationMap.get(fromEntity).keySet()) {
                int dest = singleCollect.getFileIndex(getCurrentFileId(toEntity));
                //get current cell
                CellsDTO currentCell = null;
                for (CellsDTO cell : dependsString.cells){
                    if (cell.srcFile == src && cell.destFile == dest){
                        dependsString.cells.remove(cell);
                        currentCell = cell;
                        break;
                    }
                }
                if (currentCell == null){
                    currentCell = new CellsDTO(src, dest);
                }

                for (String type : relationMap.get(fromEntity).get(toEntity).keySet()) {
                    DetailDTO detail = new DetailDTO(fromEntity, toEntity, type, 0, 0);
                    currentCell.addValue(type);
                    currentCell.details.add(detail);
                    edgeNum++;
                }
                dependsString.addCell(currentCell);
            }
        }

        return dependsString;
    }

}
