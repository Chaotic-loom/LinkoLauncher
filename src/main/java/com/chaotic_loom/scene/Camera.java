package com.chaotic_loom.scene;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    // Core State
    private final Transform transform;

    // Calculated Matrices
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    private final Vector3f front = new Vector3f(0, 0, -1);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Vector3f center = new Vector3f();

    // Projection Parameters
    private float fovRadians;
    private float aspectRatio;
    private float zNear;
    private float zFar;

    public Camera(Transform transform) {
        this.transform = transform;
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();

        recalculateViewMatrix();
    }

    public Camera() {
        this(new Transform(new Vector3f(0, 0, 3)));

        // Default perspective
        setPerspective(70.0f, 16.0f / 9.0f, 0.1f, 1000.0f);
    }

    // --- Matrix Recalculation ---
    private void recalculateViewMatrix() {
        this.viewMatrix.identity();

        front.set(0, 0, -1).rotate(transform.getRotation());
        up.set(0, 1, 0).rotate(transform.getRotation());

        center.set(transform.getPosition()).add(front);

        this.viewMatrix.lookAt(transform.getPosition(), center, up);
    }

    private void recalculateProjectionMatrix() {
        if (aspectRatio <= 0) aspectRatio = 1.0f;
        projectionMatrix.identity().perspective(fovRadians, aspectRatio, zNear, zFar);
    }

    // --- Setters for Projection ---

    //TODO: getter for calculating the aspect ratio of a resolution

    /** Sets the perspective projection parameters. */
    public void setPerspective(float fovDegrees, float aspectRatio, float zNear, float zFar) {
        this.fovRadians = (float) Math.toRadians(fovDegrees);
        this.aspectRatio = aspectRatio;
        this.zNear = zNear;
        this.zFar = zFar;
        recalculateProjectionMatrix();
    }

    /** Sets the Field of View (FOV) in degrees. */
    public void setFov(float fovDegrees) {
        this.fovRadians = (float) Math.toRadians(fovDegrees);
        recalculateProjectionMatrix();
    }

    /** Updates the aspect ratio. */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        recalculateProjectionMatrix();
    }

    // --- Getters ---

    public Transform getTransform() {
        return transform;
    }

    /** Gets the calculated view matrix. */
    public Matrix4f getViewMatrix() {
        if (transform.isDirty()) {
            recalculateViewMatrix();
            transform.setClean();
        }

        return viewMatrix;
    }

    /** Gets the calculated projection matrix. */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /** Gets the Field of View (FOV) in degrees. */
    public float getFovDegrees() {
        return (float) Math.toDegrees(this.fovRadians);
    }

    /** Gets the current aspect ratio. */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /** Gets the near clipping plane distance. */
    public float getNearPlane() {
        return zNear;
    }

    /** Gets the far clipping plane distance. */
    public float getFarPlane() {
        return zFar;
    }
}