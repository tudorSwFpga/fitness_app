package org.tensorflow.lite.examples.poseestimation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.jvm.internal.Intrinsics.Kotlin
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.sqrt

object PushUpCounter {
    private var direction = 1;
    private var count = 0.0;
    fun count(
        persons: List<Person>
    ): Int {
        persons.forEach {person ->
            val leftelbowx = person.keyPoints[BodyPart.LEFT_ELBOW.position].coordinate.x + 75;
            var leftelbowy = person.keyPoints[BodyPart.LEFT_ELBOW.position].coordinate.y;

            val rightelbowx = person.keyPoints[BodyPart.RIGHT_ELBOW.position].coordinate.x;
            var rightelbowy = person.keyPoints[BodyPart.RIGHT_ELBOW.position].coordinate.y;

            val x1 = person.keyPoints[BodyPart.LEFT_EAR.position].coordinate.x + 125;
            var y1 = person.keyPoints[BodyPart.LEFT_EAR.position].coordinate.y;

            val x3 = person.keyPoints[BodyPart.LEFT_WRIST.position].coordinate.x + 40;
            var y3 = person.keyPoints[BodyPart.LEFT_WRIST.position].coordinate.y + 100;

            val x4 = person.keyPoints[BodyPart.RIGHT_WRIST.position].coordinate.x + 20;
            var y4 = person.keyPoints[BodyPart.RIGHT_WRIST.position].coordinate.y + 100;

            val x5 = person.keyPoints[BodyPart.RIGHT_EYE.position].coordinate.x;
            var y5 = person.keyPoints[BodyPart.RIGHT_EYE.position].coordinate.y;

            // Calculate the magnitudes of the vectors
            val b1 = sqrt(
                Math.pow((x1 - leftelbowx).toDouble(), 2.0) + Math.pow((y1 - leftelbowy).toDouble(), 2.0)
                    .toDouble()) * sqrt(
                Math.pow(
                (x3 - leftelbowx).toDouble(), 2.0).toDouble() + Math.pow((y3 - leftelbowy).toDouble(), 2.0)
                    .toDouble());

            // Calculate the dot product of the vectors
            val dotProduct = (x1 - leftelbowx) * (x3 - leftelbowx) + (y1 - leftelbowy) * (y3 - leftelbowy);

            // Calculate the angle in radians
            val angle = acos(dotProduct / b1);

            // Convert the angle from radians to degrees if needed
            // let angleInDegrees = angle * (180 / Math.PI);

            val v3 = sqrt(Math.pow((x5 - rightelbowx).toDouble(), 2.0) + Math.pow((y5 - rightelbowy).toDouble(), 2.0));
            val angle2 = acos(((x4 - rightelbowx) * (x5 - rightelbowx) + (y4 - rightelbowy) * (y5 - rightelbowy)) /
                    (sqrt(Math.pow((x4 - rightelbowx).toDouble(), 2.0).toDouble() + Math.pow((y4 - rightelbowy).toDouble(), 2.0)
                        .toDouble()) * v3)
            );
            //console.log("received",57.3*angle);
            //console.log("angle2",57.3*angle2);


            if(57.3*angle>120 && 57.3*angle2>120){
                if(direction==0){
                    direction=1;
                    count+=0.5;

                }}
            else if(57.3*angle<90 && 57.3*angle2<90){
                if(direction==1){
                    direction=0;
                    count+=0.5;
                }
            }
            Log.d("PUP-COUNTER",count.toString())
        }
        return count.toInt()
    }
}