package com.regnosys.rosetta.generator.emof.object ;

import com.regnosys.rosetta.generator.emof.util.XmlHelper ;
import com.regnosys.rosetta.generator.emof.util.IdentifierGenerator ;
import com.regnosys.rosetta.generator.emof.util.DatatypeHelper;

import com.regnosys.rosetta.rosetta.simple.Attribute ;
import com.regnosys.rosetta.rosetta.simple.Data ;

import java.util.List ;
import java.util.HashMap ;
import java.util.Map ;

public class ClassifierGenerator {
    static final String FILENAME = "Types.ecore.xml" ;
    public ClassifierGenerator() { /* do nothing */ }

    public Map<String, ? extends CharSequence> generate ( List<Data> rosettaClasses , String version ) {
        Map result = new HashMap() ;
        String packages = generateListOfClasses( rosettaClasses) ;
        result.put(FILENAME, packages ) ;
        return result ;
    }

    public String generateListOfClasses ( List<Data> classList ) {
        StringBuilder sb = new StringBuilder() ;
        for ( Data thisClass : classList ) {
            sb.append( generateClass(thisClass) ) ;
        }
        return sb.toString() ;
    }

    public String generateClass ( Data in ) {
        String thisElementId = IdentifierGenerator.fromTwoParts(in.getModel().getName() , in.getName()) ;

        StringBuilder sb = new StringBuilder()
                .append ( XmlHelper.typedTagBegin( "ownedType" , "emof:Class" , false))
                .append ( XmlHelper.addAttribute( "xmi:id" , thisElementId) )
                .append ( XmlHelper.addAttribute( "name" , in.getName() ) )
                .append( XmlHelper.closeTag() ) ;

        sb.append( XmlHelper.addComment( thisElementId , in.getDefinition())) ;

        if ( in.hasSuperType() == true ) {
            String refClassId = IdentifierGenerator.fromTwoParts(in.getSuperType().getModel().getName(), in.getSuperType().getName() ) ;
            String generalId = IdentifierGenerator.fromMetaPartName( refClassId, "Generalization") ;

            sb.append( XmlHelper.untypedTagBegin( "superClass" , false) )
                .append( XmlHelper.addAttribute( "xmi:id" , generalId))
                .append( XmlHelper.addAttribute( "xmi:idref" , refClassId))
                .append(XmlHelper.endTag()) ;
        }

        for ( Attribute thisAttribute : in.getAttributes()) {
            sb.append( generateAttribute(thisAttribute , thisElementId )) ;
        }

        sb.append( XmlHelper.endBlockTag( "ownedType")) ;
        return sb.toString() ;
    }

    private String generateAttribute( Attribute in , String owningClassId ) {
        String thisElementId = IdentifierGenerator.fromTwoParts(owningClassId , in.getName() ) ;
/*
        String thisElementId = IdentifierGenerator.fromThreeParts
            (   in.getClass().getPackage().getName()
            ,   in.getClass().getName()
            ,   in.getName()
            ) ;
*/

        StringBuilder sb = new StringBuilder()
                .append ( XmlHelper.untypedTagBegin( "ownedAttribute" , false))
                .append ( XmlHelper.addAttribute( "xmi:id" , thisElementId) )
                .append ( XmlHelper.addAttribute( "name" , in.getName() ) ) ;

        if ( DatatypeHelper.isPrimitiveType( in.getType().getName() ) == true) {
            sb.append(XmlHelper.addAttribute("type", DatatypeHelper.mapToPrimitiveType(in.getType().getName())));
        } else {
            sb.append(XmlHelper.addAttribute("type"
               , IdentifierGenerator.fromTwoParts(
                   in.getType().getModel().getName()
               ,   in.getType().getName()
               )
            ) ) ;
        }

        sb.append(XmlHelper.addAttribute("lower", String.valueOf(in.getCard().getInf()) )) ;

        if ( in.getCard().isUnbounded() == true ) {
            sb.append(XmlHelper.addAttribute("upper", "*"));
        } else {
            sb.append(XmlHelper.addAttribute("upper", String.valueOf(in.getCard().getSup()) )) ;
        }

        sb.append(XmlHelper.closeTag()) ;
        sb.append( XmlHelper.addComment( thisElementId , in.getDefinition())) ;
        sb.append( XmlHelper.endBlockTag( "ownedAttribute")) ;
        return sb.toString() ;
    }
}