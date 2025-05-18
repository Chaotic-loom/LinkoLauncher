package com.chaotic_loom.scene;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    private final Vector3f position;
    private final Quaternionf rotation;
    private final Vector3f scale;

    // Reusable temporary quaternion to avoid allocations in rotation methods
    private final Quaternionf tempRotation = new Quaternionf();

    private boolean dirty = true;

    public Transform() {
        this.position = new Vector3f();
        this.rotation = new Quaternionf();
        this.scale = new Vector3f(1, 1, 1);
    }

    public Transform(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
        this.rotation = new Quaternionf();
        this.scale = new Vector3f(1, 1, 1);
    }

    public Transform(Vector3f position) {
        this.position = position;
        this.rotation = new Quaternionf();
        this.scale = new Vector3f(1, 1, 1);
    }

    public Transform(Vector3f position, Quaternionf rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    // Getters

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getScale() {
        return this.scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    // Setters

    public void setPosition(Vector3f position) {
        this.position.set(position.x, position.y, position.z);
        setDirty();
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        setDirty();
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
        setDirty();
    }

    // Convenience method for Euler angles (use with caution - gimbal lock)
    public void setRotation(float angleX, float angleY, float angleZ) {
        this.rotation.identity().rotateXYZ(Math.toRadians(angleX), Math.toRadians(angleY), Math.toRadians(angleZ));
        setDirty();
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        setDirty();
    }

    public void setScale(float scale) {
        this.setScale(scale, scale, scale);
    }

    public void translate(float dx, float dy, float dz) {
        this.position.add(dx, dy, dz);
        setDirty();
    }

    public void rotateLocal(float angle, float axisX, float axisY, float axisZ) {
        float radAngle = Math.toRadians(angle);
        tempRotation.identity().rotateAxis(radAngle, axisX, axisY, axisZ);

        this.rotation.mul(tempRotation, this.rotation);
        setDirty();
    }

    public void rotateGlobal(float angle, float axisX, float axisY, float axisZ) {
        float radAngle = Math.toRadians(angle);
        tempRotation.identity().rotateAxis(radAngle, axisX, axisY, axisZ);

        this.rotation.premul(tempRotation, this.rotation);
        setDirty();
    }

    /**
     * Rotates the transform around a world-space pivot point.
     *
     * @param pivot The world-space pivot point to rotate around.
     * @param angle Degrees to rotate.
     * @param axisX X component of the rotation axis.
     * @param axisY Y component of the rotation axis.
     * @param axisZ Z component of the rotation axis.
     * @param applyRotation If true, apply the rotation to the object's orientation; if false, only position is rotated.
     */
    public void rotateAround(Vector3f pivot, float angle, float axisX, float axisY, float axisZ, boolean applyRotation) {
        float rad = Math.toRadians(angle);
        tempRotation.identity().rotateAxis(rad, axisX, axisY, axisZ);

        // Move position relative to pivot
        position.sub(pivot);
        // Rotate the position
        tempRotation.transform(position);
        // Move back
        position.add(pivot);

        // Optionally rotate orientation
        if (applyRotation) {
            rotation.premul(tempRotation);
        }
        setDirty();
    }

    /**
     * Overload: rotates around a pivot using a Vector3f axis.
     */
    public void rotateAround(Vector3f pivot, float angle, Vector3f axis, boolean applyRotation) {
        rotateAround(pivot, angle, axis.x, axis.y, axis.z, applyRotation);
    }

    public void rotateAround(Vector3f pivot, float angle, float x, float y, float z) {
        rotateAround(pivot, angle, x, y, z, false);
    }

    public void rotateAround(Vector3f pivot, float angle, Vector3f axis) {
        rotateAround(pivot, angle, axis, false);
    }

    /**
     * Pitch: rotate around local right axis.
     */
    public void pitch(float angleDeg) {
        // Compute local right vector
        Vector3f right = new Vector3f(1, 0, 0);
        rotation.transform(right);
        rotateLocal(angleDeg, right.x, right.y, right.z);
    }

    /**
     * Yaw: rotate around world up (0,1,0).
     */
    public void yaw(float angleDeg) {
        rotateGlobal(angleDeg, 0, 1, 0);
    }

    /**
     * Roll: rotate around local forward axis (0,0,-1).
     */
    public void roll(float angleDeg) {
        Vector3f forward = new Vector3f(0, 0, -1);
        rotation.transform(forward);
        rotateLocal(angleDeg, forward.x, forward.y, forward.z);
    }

    /**
     * Orients the transform to look at a target point in world space.
     * @param target The point to look at.
     * @param upHint World-space up direction.
     */
    public void lookAt(Vector3f target, Vector3f upHint) {
        Vector3f dir = new Vector3f(target).sub(position).normalize();
        // Quaternionf.lookAlong expects the *reverse* direction
        rotation.identity().lookAlong(new Vector3f(dir).negate(), upHint);
        setDirty();
    }

    /**
     * Convenience: lookAt with world Y-up.
     */
    public void lookAt(Vector3f target) {
        lookAt(target, new Vector3f(0, 1, 0));
    }

    // Dirty

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        this.dirty = true;
    }

    public void setClean() {
        this.dirty = false;
    }
}
