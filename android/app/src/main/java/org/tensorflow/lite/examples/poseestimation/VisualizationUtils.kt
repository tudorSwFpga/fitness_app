/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.TextView
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Person
import java.lang.Math.pow
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.pow
import kotlin.math.acos

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 6f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 4f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    private var direction = 1;
    private var count = 0.0;

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
        }
        val paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLUE
            textAlign = Paint.Align.LEFT
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        persons.forEach { person ->
            // draw person id if tracker is enable
            if (isTrackerEnabled) {
                person.boundingBox?.let {
                    val personIdX = max(0f, it.left)
                    val personIdY = max(0f, it.top)

                    originalSizeCanvas.drawText(
                        person.id.toString(),
                        personIdX,
                        personIdY - PERSON_ID_MARGIN,
                        paintText
                    )
                    originalSizeCanvas.drawRect(it, paintLine)
                }
            }
            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }

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
            val b1 = sqrt(pow((x1 - leftelbowx).toDouble(), 2.0) + pow((y1 - leftelbowy).toDouble(), 2.0).toDouble()) * sqrt(pow(
                (x3 - leftelbowx).toDouble(), 2.0).toDouble() + pow((y3 - leftelbowy).toDouble(), 2.0).toDouble());

            // Calculate the dot product of the vectors
            val dotProduct = (x1 - leftelbowx) * (x3 - leftelbowx) + (y1 - leftelbowy) * (y3 - leftelbowy);

            // Calculate the angle in radians
            val angle = acos(dotProduct / b1);

            // Convert the angle from radians to degrees if needed
            // let angleInDegrees = angle * (180 / Math.PI);

            val v3 = sqrt(pow((x5 - rightelbowx).toDouble(), 2.0) + pow((y5 - rightelbowy).toDouble(), 2.0));
            val angle2 = acos(((x4 - rightelbowx) * (x5 - rightelbowx) + (y4 - rightelbowy) * (y5 - rightelbowy)) /
            (sqrt(Math.pow((x4 - rightelbowx).toDouble(), 2.0).toDouble() + pow((y4 - rightelbowy).toDouble(), 2.0).toDouble()) * v3)
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

            originalSizeCanvas.drawText(
                "Push-up count : "+count.toString(),
                10.0F,
                20.0F,
                paintText
            )
        }
        return output
    }
}
