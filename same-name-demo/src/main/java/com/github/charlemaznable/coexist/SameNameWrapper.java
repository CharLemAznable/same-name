package com.github.charlemaznable.coexist;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;

import static com.github.charlemaznable.core.lang.ClzPath.classResource;

public class SameNameWrapper {

    private Object v1;
    private Object v2;

    @SneakyThrows
    public SameNameWrapper() {
        val sameNameClassName = "com.github.charlemaznable.coexist.SameName";

        try (val jarV1 = newJarFile("same-name-0.0.1.jar")) {
            val classV1 = classForNameInJarFile(sameNameClassName, jarV1);
            v1 = classV1.getConstructor().newInstance();
        }

        try (val jarV2 = newJarFile("same-name-0.0.2.jar")) {
            val classV2 = classForNameInJarFile(sameNameClassName, jarV2);
            v2 = classV2.getConstructor().newInstance();
        }
    }

    @SneakyThrows
    public String descriptionV1() {
        return (String) v1.getClass().getMethod("description").invoke(v1);
    }

    @SneakyThrows
    public String descriptionV2() {
        return (String) v2.getClass().getMethod("description").invoke(v2);
    }

    @SneakyThrows
    private JarFile newJarFile(String classPath) {
        return new JarFile(new File(classResource(classPath).getFile()));
    }

    @SuppressWarnings("SameParameterValue")
    @SneakyThrows
    private Class<?> classForNameInJarFile(String className, JarFile jarFile) {
        return Class.forName(className, true, new JarFileClassLoader(jarFile));
    }

    @Slf4j
    @AllArgsConstructor
    private static class JarFileClassLoader extends ClassLoader {

        private JarFile jarFile;

        @SneakyThrows
        @Override
        protected Class<?> findClass(String name) {
            val entryName = name.replaceAll("[.]", "/") + ".class";
            val entry = jarFile.getJarEntry(entryName);
            if (entry == null)
                throw new ClassNotFoundException("Jar entry not found");

            val data = new byte[(int) entry.getSize()];
            @Cleanup InputStream in = jarFile.getInputStream(entry);
            val result = in.read(data);
            if (result < 0) log.warn("read error");
            return defineClass(name, data, 0, data.length);
        }
    }
}
