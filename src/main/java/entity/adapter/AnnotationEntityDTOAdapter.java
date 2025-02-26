package entity.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import entity.dto.AnnotationEntityDTO;

import java.io.IOException;

public class AnnotationEntityDTOAdapter extends TypeAdapter<AnnotationEntityDTO> {

  private final AdditionalBinDTOAdapter additionalBinDTOAdapter;
  private final LocationDTOAdapter locationDTOAdapter;

  public AnnotationEntityDTOAdapter() {
    this.additionalBinDTOAdapter = new AdditionalBinDTOAdapter();
    this.locationDTOAdapter = new LocationDTOAdapter();
  }

  @Override
  public void write(JsonWriter out, entity.dto.AnnotationEntityDTO value) throws IOException {
    out.beginObject();
    out.name("external").value(value.isExternal());
    out.name("id").value(value.getId());
    out.name("name").value(value.getName());
    out.name("qualifiedName").value(value.getQualifiedName());
    out.name("category").value(value.getCategory());
    out.name("parentId").value(value.getParentId());
    out.name("File").value(value.getFile());
    out.name("additionalBin");
    this.additionalBinDTOAdapter.write(out, value.getAdditionalBin());
    out.name("location");
    this.locationDTOAdapter.write(out, value.getLocation());
    out.name("modifiers").value(value.getModifiers());
    out.name("rawType").value(value.getRawType());
    out.endObject();
  }

  @Override
  public AnnotationEntityDTO read(JsonReader in) throws IOException {
    AnnotationEntityDTO res = new AnnotationEntityDTO();
    in.beginObject();
    while (in.hasNext()) {
      switch (in.nextName()) {
        case "external":
          res.setExternal(in.nextBoolean());
          break;
        case "id":
          res.setId(in.nextInt());
          break;
        case "name":
          res.setName(in.nextString());
          break;
        case "qualifiedName":
          res.setQualifiedName(in.nextString());
          break;
        case "category":
          res.setCategory(in.nextString());
          break;
        case "parentId":
          res.setParentId(in.nextInt());
          break;
        case "File":
          res.setFile(in.nextString());
          break;
        case "additionalBin":
          res.setAdditionalBin(this.additionalBinDTOAdapter.read(in));
          break;
        case "location":
          res.setLocation(this.locationDTOAdapter.read(in));
          break;
        case "modifiers":
          res.setModifiers(in.nextString());
          break;
        case "rawType":
          res.setRawType(in.nextString());
          break;
      }
    }
    in.endObject();
    return res;
  }
}
