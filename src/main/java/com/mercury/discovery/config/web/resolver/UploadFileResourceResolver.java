package com.mercury.discovery.config.web.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UploadFileResourceResolver extends PathResourceResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileResourceResolver.class);

    private final Set<String> allowedExtensionSet;

    private boolean isCheckAllowedExtension = false;

    public UploadFileResourceResolver(Set<String> allowedExtensionSet) {

        this.allowedExtensionSet = allowedExtensionSet != null ? allowedExtensionSet : new HashSet<>();
        if (this.allowedExtensionSet.size() > 0) {
            isCheckAllowedExtension = true;
            LOGGER.info("allowedExtensionSet {}", allowedExtensionSet);
        }
    }

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        Resource resource = location.createRelative(resourcePath);
        if (resource.isReadable()) {
            if (!checkAllowedExtensions(resource)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Resource path \"" + resourcePath + "\" was successfully resolved " + "but resource \""
                            + resource.getURL() + "\" is not in apps.upload.allowedAccessExtensions ");
                }
                return null;
            }

            if (checkResource(resource, location)) {
                return resource;
            } else if (LOGGER.isDebugEnabled()) {
                Resource[] allowedLocations = getAllowedLocations();
                LOGGER.debug("Resource path \"" + resourcePath + "\" was successfully resolved " + "but resource \""
                        + resource.getURL() + "\" is neither under the " + "current location \"" + location.getURL()
                        + "\" nor under any of the " + "allowed locations "
                        + (allowedLocations != null ? Arrays.asList(allowedLocations) : "[]"));
            }
        }
        return null;
    }

    private boolean checkAllowedExtensions(Resource resource) {
        if (isCheckAllowedExtension && allowedExtensionSet != null) {
            String extension = getExtension(resource.getFilename());
            return allowedExtensionSet.contains(extension);
        }
        return true;
    }

    private String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = filename.lastIndexOf('.');
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1).toLowerCase();
        }
    }
}
