package de.maxhenkel.car.entity.car.parts;

import de.maxhenkel.car.entity.car.base.EntityGenericCar;
import de.maxhenkel.car.entity.model.obj.OBJModel;
import de.maxhenkel.car.entity.model.obj.OBJModelInstance;
import de.maxhenkel.car.entity.model.obj.OBJModelOptions;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class PartWheelBase extends PartModel {

    protected float rotationModifier;
    protected float stepHeight;

    public PartWheelBase(OBJModel model, float rotationModifier, float stepHeight) {
        super(model);
        this.rotationModifier = rotationModifier;
        this.stepHeight=stepHeight;
    }

    public float getStepHeight(){
        return stepHeight;
    }

    @Override
    public boolean validate(List<Part> parts, List<ITextComponent> messages) {
        for(Part part:parts){
            if(part instanceof PartBody){
                PartBody body= (PartBody) part;
                if(!body.canFitWheel(this)){
                    messages.add(new TextComponentTranslation("message.parts.wrong_wheel_type"));
                    return false;
                }
            }
        }

        return super.validate(parts, messages);
    }

    @Override
    public List<OBJModelInstance> getInstances(EntityGenericCar car) {
        List<OBJModelInstance> list = new ArrayList<>();

        Vec3d[] wheelOffsets = new Vec3d[0];

        for (Part part : car.getModelParts()) {
            if (part instanceof PartBody) {
                wheelOffsets = ((PartBody) part).getWheelOffsets();
            }
        }

        List<PartWheelBase> wheels=new ArrayList<>();

        for (Part part : car.getModelParts()) {
            if (part instanceof PartWheelBase) {
                wheels.add((PartWheelBase) part);
            }
        }

        for (int i=0; i<wheelOffsets.length&&i<wheels.size(); i++) {
            list.add(new OBJModelInstance(wheels.get(i).model, new OBJModelOptions(wheelOffsets[i], rotationModifier)));
        }

        return list;
    }
}