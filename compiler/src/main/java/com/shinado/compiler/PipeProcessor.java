package com.shinado.compiler;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class PipeProcessor extends AbstractProcessor{

    private ProcessingEnvironment env;

    @Override
    public synchronized void init(ProcessingEnvironment pe) {
        this.env = pe;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (TypeElement te : annotations) {
                final Set< ? extends Element> elts = roundEnv.getElementsAnnotatedWith(te);
                for (Element elt : elts) {

                    env.getMessager().printMessage(Diagnostic.Kind.WARNING,
                            String.format("%s : thou shalt not hack %s", roundEnv.getRootElements(), elt),
                            elt);
                }
            }
        }
        return true;
    }
}
