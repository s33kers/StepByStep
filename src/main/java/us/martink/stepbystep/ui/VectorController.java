package us.martink.stepbystep.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import us.martink.stepbystep.services.Channel;
import us.martink.stepbystep.services.Encoder;
import us.martink.stepbystep.ui.model.Vector;
import us.martink.stepbystep.ui.model.VectorRequestForm;
import us.martink.stepbystep.ui.utils.ValidationUtils;

import java.util.List;

/**
 * Created by tadas.
 */

@Controller
public class VectorController {

    @RequestMapping("/vector")
    public String vector (Model model) {
        model.addAttribute("requestVector", new VectorRequestForm());
        return "vector";
    }

    @RequestMapping(value="/vector", method= RequestMethod.POST, params="action=Užkoduoti")
    public String encode(@ModelAttribute VectorRequestForm vectorRequest, Model model) {
        String validation = ValidationUtils.validateBeforeVectorEncoding(vectorRequest);
        if (validation != null) {
            model.addAttribute("validation", validation);
            model.addAttribute("requestVector", vectorRequest);
            return "vector";
        }

        //encode vector
        vectorRequest.setEncodedVector(new Vector());
        vectorRequest.getEncodedVector().setVector(Encoder.encodeVector(vectorRequest.getMatrix().getMatrix(), vectorRequest.getSimpleVector().getVector()));
        vectorRequest.getEncodedVector().setVectorText(Vector.vectorToString(vectorRequest.getEncodedVector().getVector(), ""));

        //transfer vector
        int[] transferredVector = vectorRequest.getEncodedVector().getVector().clone();
        vectorRequest.setMistakes(new Vector());
        List<Integer> mistakesList = Channel.sendThroughChannel(vectorRequest.getP(), transferredVector);
        vectorRequest.getMistakes().setVector(mistakesList.stream().mapToInt(i->i).toArray());
        vectorRequest.getMistakes().setVectorText(Vector.vectorToString(vectorRequest.getMistakes().getVector(), " "));
        vectorRequest.setTransferredVector(new Vector());
        vectorRequest.getTransferredVector().setVector(transferredVector);
        vectorRequest.getTransferredVector().setVectorText(Vector.vectorToString(transferredVector, ""));

        model.addAttribute("requestVector", vectorRequest);
        return "vector";
    }

    @RequestMapping(value="/vector", method= RequestMethod.POST, params="action=Dekoduoti")
    public String decode(@ModelAttribute VectorRequestForm vectorRequest, Model model) {
        String validation = ValidationUtils.validateBeforeVectorEncoding(vectorRequest);
        if (validation != null) {
            model.addAttribute("validation", validation);
            model.addAttribute("requestVector", vectorRequest);
            return "vector";
        }
        return "";
    }
}
