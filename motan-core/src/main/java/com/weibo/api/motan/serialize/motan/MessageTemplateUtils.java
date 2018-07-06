package com.weibo.api.motan.serialize.motan;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.nio.file.Paths;

/**
 * @author sunnights
 */
public class MessageTemplateUtils {

    public static void generate(Class clazz, String path) {
        try {
            System.out.println("generate: " + clazz.getName());

            MethodSpec toMessage = MethodSpec.methodBuilder("toMessage")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(clazz, "obj")
                    .returns(GenericMessage.class)
                    .addStatement("$T message = new $T()", GenericMessage.class, GenericMessage.class)
                    .addStatement("Class clazz = obj.getClass()")
                    .addStatement("int i = 0")
                    .beginControlFlow("while (clazz != Object.class)")
                    .addStatement("i = setMessage(message, obj, clazz, i)")
                    .addStatement("clazz = clazz.getSuperclass()")
                    .endControlFlow()
                    .addStatement("return message")
                    .build();
            MethodSpec fromMessage = MethodSpec.methodBuilder("fromMessage")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(GenericMessage.class, "message")
                    .returns(clazz)
                    .addStatement("$T obj = new $T()", clazz, clazz)
                    .addStatement("Class clazz = obj.getClass()")
                    .addStatement("int i = 0")
                    .beginControlFlow("while (clazz != Object.class)")
                    .addStatement("i = setObject(message, obj, clazz, i)")
                    .addStatement("clazz = clazz.getSuperclass()")
                    .endControlFlow()
                    .addStatement("return obj")
                    .build();
            TypeSpec typeSpec = TypeSpec.classBuilder(clazz.getSimpleName() + "MessageTemplate")
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ParameterizedTypeName.get(AbstractMessageTemplate.class, clazz))
                    .addMethod(toMessage)
                    .addMethod(fromMessage)
                    .build();
            JavaFile javaFile = JavaFile.builder(clazz.getPackage().getName(), typeSpec)
                    .build();
            javaFile.writeTo(Paths.get(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
