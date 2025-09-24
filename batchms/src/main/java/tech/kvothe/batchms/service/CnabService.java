package tech.kvothe.batchms.service;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class CnabService {

    private final Path fileStorageLocation;
    private final Job job;

    public CnabService(@Value("${file.upload-dir}") String fileUploadDir, Job job) {
        this.fileStorageLocation = Paths.get(fileUploadDir);
        this.job = job;
    }

    public void uploadCnabFile(MultipartFile file) throws IOException {
        var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        var targetLocation = fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);
    }
}
