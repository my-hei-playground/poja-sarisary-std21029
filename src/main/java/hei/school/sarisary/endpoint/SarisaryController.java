package hei.school.sarisary.endpoint;

import hei.school.sarisary.file.BucketComponent;
import hei.school.sarisary.file.FileHash;
import hei.school.sarisary.service.event.SaryService;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class SarisaryController {
  private final BucketComponent bucketComponent;
  private final SaryService saryService;

  @PutMapping(value = "/black-and-white/{id}", consumes = "image/png")
  public ResponseEntity<Void> upload_and_transform(
      @PathVariable String id, @RequestBody byte[] imageData) throws IOException {
    String IMAGE_FILE_SUFFIX = ".jpg";
    String bucketKeyOriginal = id + IMAGE_FILE_SUFFIX;
    String bucketKeyTransformed = id + "_transformed" + IMAGE_FILE_SUFFIX;
    File originalImageFile = saryService.convertByteArrayToFile(imageData, id);
    File tranformedImageFile = saryService.convertByteArrayToGrayScaleFile(imageData, id);
    FileHash originalFileHash = bucketComponent.upload(originalImageFile, bucketKeyOriginal);
    FileHash transformedFileHash =
        bucketComponent.upload(tranformedImageFile, bucketKeyTransformed);
    System.out.println("Original" + originalFileHash);
    System.out.println("Transformed" + transformedFileHash);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/black-and-white/{id}")
  public ResponseEntity<SaryResponse> get_presigned_image_url(@PathVariable String id)
      throws IOException {

    Duration expiration = Duration.ofHours(24);
    String originalUrl = bucketComponent.presign(id, expiration).toString();
    String transformedUrl = bucketComponent.presign(id + "_transformed", expiration).toString();

    return ResponseEntity.ok(
        SaryResponse.builder().original_url(originalUrl).transformed_url(transformedUrl).build());
  }
}
