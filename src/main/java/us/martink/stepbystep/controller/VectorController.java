package us.martink.stepbystep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import us.martink.stepbystep.services.Channel;
import us.martink.stepbystep.services.Decoder;
import us.martink.stepbystep.services.Encoder;
import us.martink.stepbystep.ui.model.Matrix;
import us.martink.stepbystep.ui.model.Vector;
import us.martink.stepbystep.ui.model.VectorRequestForm;
import us.martink.stepbystep.ui.utils.ValidationUtils;

import java.util.List;

/**
 * Created by tadas.
 */

@Controller
public class VectorController {

    @RequestMapping({"/", "/vector"})
    public String vector (Model model) {
        model.addAttribute("requestForm", new VectorRequestForm());
        return "vector";
    }

    @RequestMapping(value={"/", "/vector"}, method= RequestMethod.POST, params="action=Užkoduoti")
    public String encode(@ModelAttribute VectorRequestForm vectorRequest, Model model) {
        String validation = ValidationUtils.validateBeforeVectorEncoding(vectorRequest);
        if (validation != null) {
            vectorRequest.setDecodedVector(null);
            vectorRequest.setTransferredVector(null);
            vectorRequest.setMistakes(null);
            vectorRequest.setEncodedVector(null);
            model.addAttribute("validation", validation);
            model.addAttribute("requestForm", vectorRequest);
            return "vector";
        }

        //Uzkoduojamas vektorius
        vectorRequest.setEncodedVector(new Vector());
        vectorRequest.getEncodedVector().setVector(Encoder.encodeVector(vectorRequest.getMatrix().getMatrix(), vectorRequest.getSimpleVector().getVector()));
        vectorRequest.getEncodedVector().setVectorText(Vector.vectorToString(vectorRequest.getEncodedVector().getVector(), ""));

        //Siunciamas vektorius kanalu
        int[] transferredVector = vectorRequest.getEncodedVector().getVector().clone();
        vectorRequest.setMistakes(new Vector());
        List<Integer> mistakesList = Channel.sendThroughChannel(vectorRequest.getP(), transferredVector);
        vectorRequest.getMistakes().setVector(mistakesList.stream().mapToInt(i->i).toArray());
        vectorRequest.getMistakes().setVectorText(Vector.vectorToString(vectorRequest.getMistakes().getVector(), " "));
        vectorRequest.setTransferredVector(new Vector());
        vectorRequest.getTransferredVector().setVector(transferredVector);
        vectorRequest.getTransferredVector().setVectorText(Vector.vectorToString(transferredVector, ""));

        model.addAttribute("requestForm", vectorRequest);
        return "vector";
    }

    @RequestMapping(value={"/", "/vector"}, method= RequestMethod.POST, params="action=Dekoduoti")
    public String decode(@ModelAttribute VectorRequestForm vectorRequest, Model model) {
        String validation = ValidationUtils.validateBeforeVectorDecoding(vectorRequest);
        if (validation != null) {
            model.addAttribute("validation", validation);
            model.addAttribute("requestForm", vectorRequest);
            return "vector";
        }

        //Perkuriami gauti teksto pavidalo duomenys
        vectorRequest.getMatrix().setMatrix(Matrix.textToMatrix(vectorRequest.getMatrix().getMatrixText(), vectorRequest.getK(), vectorRequest.getN()));
        vectorRequest.getSimpleVector().setVector(Vector.textToVector(vectorRequest.getSimpleVector().getVectorText()));
        vectorRequest.getMistakes().setVector(Vector.textToVector(vectorRequest.getMistakes().getVectorText()));
        vectorRequest.getEncodedVector().setVector(Vector.textToVector(vectorRequest.getEncodedVector().getVectorText()));
        vectorRequest.getTransferredVector().setVector(Vector.textToVector(vectorRequest.getTransferredVector().getVectorText()));

        //Gautas vektorius atkoduojamas
        Decoder decoder = new Decoder(vectorRequest.getMatrix());
        vectorRequest.setDecodedVector(new Vector());
        vectorRequest.getDecodedVector().setVector(decoder.decodeVector(vectorRequest.getTransferredVector().getVector()));
        vectorRequest.getDecodedVector().setVectorText(Vector.vectorToString(vectorRequest.getDecodedVector().getVector(), ""));

        model.addAttribute("requestForm", vectorRequest);
        return "vector";
    }
}
