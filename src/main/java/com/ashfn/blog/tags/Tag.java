package com.ashfn.blog.tags;

import com.ashfn.blog.tags.exception.InvalidColorException;
import com.ashfn.blog.tags.exception.InvalidDescriptionException;
import com.ashfn.blog.tags.exception.InvalidTagNameException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tag {

    /**
     * Short name with no spaces
     */
    public final String name;

    /**
     * Longer description formatted in markdown
     */
    public final String description;

    /**
     * Color used to differentiate the tag visually
     */
    public final Color color;

    /**
     * @param name Alphanumeric and dashes name of tag less than 15 characters
     * @param description Longer description of the tag formatted in markdown, less than 200 characters
     * @param color Color used to differentiate the tag visually
     */
    public Tag(String name, String description, Color color) throws InvalidTagNameException, InvalidDescriptionException {

        if(!validTagName(name)){
            throw new InvalidTagNameException(String.format("Provided name %s does not meet requirements of less than 15 alphanumeric or dash characters", name));
        }

        if(!validDescription(description)){
            throw new InvalidDescriptionException((String.format("Provided description %s does not meet requirments of less than 200 characters", description)));
        }

        this.name = name;
        this.description = description;
        this.color = color;
    }

    public static Tag loadTag(JsonObject jsonTag) throws InvalidTagNameException, InvalidDescriptionException, InvalidColorException  {
        if(!jsonTag.has("name")) throw new InvalidTagNameException("No name property found on the provided json object");
        if(!jsonTag.has("description")) throw new InvalidDescriptionException("No description property found on the provided json object");
        if(!jsonTag.has("color")) throw new InvalidColorException("No color property found on the provided json object");

        JsonElement nameElement = jsonTag.get("name");
        JsonElement descriptionElement = jsonTag.get("description");
        JsonElement colorElement = jsonTag.get("color");

        if(nameElement==null || !nameElement.isJsonPrimitive()) throw new InvalidTagNameException("Invalid value found on name property on provided json object, it should be a string");
        if(descriptionElement==null || !descriptionElement.isJsonPrimitive()) throw new InvalidDescriptionException("Invalid value found on description property on provided json object, it should be a string");
        if(colorElement==null || !colorElement.isJsonPrimitive()) throw new InvalidColorException("Invalid value found on color property on provided json object, it should be a string");

        JsonPrimitive namePrimitive = nameElement.getAsJsonPrimitive();
        JsonPrimitive descriptionPrimitive = descriptionElement.getAsJsonPrimitive();
        JsonPrimitive colorPrimitive = colorElement.getAsJsonPrimitive();

        if(!namePrimitive.isString()) throw new InvalidTagNameException("Invalid value found on name property on provided json object, it should be a string");
        if(!descriptionPrimitive.isString()) throw new InvalidDescriptionException("Invalid value found on description property on provided json object, it should be a string");
        if(!colorPrimitive.isString()) throw new InvalidColorException("Invalid value found on color property on provided json object, it should be a string");

        String name = namePrimitive.getAsString();
        String description = descriptionPrimitive.getAsString();
        Color color = getColor(colorPrimitive.getAsString());

        if(color == null) throw new InvalidColorException("Invalid hex color found on color property on provided json object, must be in format #FFFFFF");

        return new Tag(name, description, color);

    }

    private boolean validTagName(String input) {
        String pattern = "^[a-zA-Z0-9-]*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);
        return matcher.matches() && input.length() < 15;
    }

    private boolean validDescription(String input) {
        return input.length() < 200;
    }

    private static Color getColor(String hexCode){
        try{
            return Color.decode(hexCode);
        }catch (NumberFormatException exception){
            return null;
        }
    }

}

