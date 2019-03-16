/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
 //TO RUN CODE: PRESS SHIFT + F5 THEN TYPE "453" AND HIT ENTER
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import com.ctre.phoenix.motorcontrol.can.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.InputMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  Joystick stick;
  Joystick tankStick;
  WPI_TalonSRX backLeft;
  WPI_TalonSRX backRight;
  WPI_TalonSRX topLeft;
  WPI_TalonSRX topRight;
 CANSparkMax armControl;
  CANSparkMax wristControl;
  CANSparkMax elevatorControl;
  CANEncoder wrist;
  CANEncoder arm;
  CANEncoder elevator;
  Spark intake;
  Compressor c;
  boolean enabled;
   boolean pressureSwitch;
   double current;
   DoubleSolenoid pusher; 
  SpeedControllerGroup leftSped;
  SpeedControllerGroup rightSped;
  SpeedControllerGroup elevatorSped;
  DifferentialDrive drive;
  DifferentialDrive die;
  boolean meme;
  boolean mememe;
  boolean memememe;
  double e;
  double d;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
      UsbCamera camera2 = CameraServer.getInstance().startAutomaticCapture();
      camera.setResolution(150, 100);
      camera2.setResolution(150, 100);

      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 150, 100);
      
      Mat source = new Mat();
      Mat output = new Mat();
      
      while(!Thread.interrupted()) {
          cvSink.grabFrame(source);
          Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
          outputStream.putFrame(output);
      }
  }).start();
    stick = new Joystick(0);
    tankStick = new Joystick(1);
    topLeft = new WPI_TalonSRX(4);
    backLeft = new WPI_TalonSRX(3);
    topRight = new WPI_TalonSRX(1);
    backRight = new WPI_TalonSRX(2);
    
    intake = new Spark(5);
    armControl = new CANSparkMax(6, MotorType.kBrushless);
    wristControl = new CANSparkMax(8, MotorType.kBrushless);
    elevatorControl = new CANSparkMax(7, MotorType.kBrushless);
    
    wrist = new CANEncoder(wristControl);
    arm = new CANEncoder(armControl);
    elevator = new CANEncoder(elevatorControl);
    
    leftSped = new SpeedControllerGroup(topLeft, backLeft);
    rightSped = new SpeedControllerGroup(topRight, backRight);
    rightSped.setInverted(true);
    
    drive = new DifferentialDrive(leftSped, rightSped);

    
  c = new Compressor(0);
  c.setClosedLoopControl(true); //set me to true when ready
  enabled = c.enabled();
    pressureSwitch  = c.getPressureSwitchValue();
    current   = c.getCompressorCurrent();
    pusher  = new DoubleSolenoid(0, 1);
   
    
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
   // AnalogInput pressureSensor = new AnalogInput(0);
   // int raw = pressureSensor.getValue();
   // double volts = pressureSensor.getVoltage();
   // int averageRaw = pressureSensor.getAverageValue();
   // double averageVolts = pressureSensor.getAverageVoltage();
   // double psi = 250*(raw/volts) - 25;
   // SmartDashboard.putNumber("psi", psi);
}

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
     m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putString("meme", Boolean.toString(meme));
    meme = tankStick.getRawButton(1);
    mememe = stick.getRawButton(1);
    memememe = tankStick.getRawButton(2);
    e = -0.5*(stick.getRawAxis(3)-1);
    d = -0.5*(tankStick.getRawAxis(3)-1);
    if (tankStick.getRawButton(6) == true && tankStick.getRawButton(4) == false){
        pusher.set(DoubleSolenoid.Value.kForward);
       }
       if (tankStick.getRawButton(4) == true && tankStick.getRawButton(6) == false){
        pusher.set(DoubleSolenoid.Value.kReverse);
       }
   
    else {
      if (mememe == true){
        drive.arcadeDrive(stick.getRawAxis(2)*e,stick.getRawAxis(1)*d*-1 );
      }
      else{
        drive.arcadeDrive(stick.getRawAxis(2)*0.75*e,stick.getRawAxis(1)*d*-0.75 );
        
        
        
      }
      if (tankStick.getRawButton(3) == true ){
        intake.set(.25);
    }
     else if (tankStick.getRawButton(5) == true){
        intake.set(-.75);
    }
    else {
        intake.set(0);
    }
    }
 /*   if (tankStick.getRawButton(11)== true){
      while (wristControl.getEncoder().getPosition() > -233){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0);
          
  }
  if (tankStick.getRawButton(9)== true){
      while (wristControl.getEncoder().getPosition() > -467){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0); //hey
          
  }
  if (tankStick.getRawButton(7)== true){
      while (wristControl.getEncoder().getPosition() > -700){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0);
          
  }
  if (tankStick.getRawButton(2) == true){
    while(wristControl.getEncoder().getPosition() < 0){
        wristControl.set(1);
        
    }
  }
      else {
        wristControl.set(0);
        
      }
      if (tankStick.getRawButton(12)== true){
        while (elevatorControl.getEncoder().getPosition() > -233){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0);
            
    }
    if (tankStick.getRawButton(10)== true){
        while (elevatorControl.getEncoder().getPosition() > -467){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0); //hey
            
    }
    if (tankStick.getRawButton(8)== true){
        while (elevatorControl.getEncoder().getPosition() > -700){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0);
            
    }
    if (tankStick.getRawButton(3) == true){
      while(elevatorControl.getEncoder().getPosition() < 0){
          elevatorControl.set(1);
          
      }
    }
        else {
          elevatorControl.set(0);
          
        }
        if (stick.getRawButton(11)== true){
          while (armControl.getEncoder().getPosition() > -67){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0);
              
      }
      if (stick.getRawButton(9)== true){
          while (armControl.getEncoder().getPosition() > -133){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0); //hey
              
      }
      if (stick.getRawButton(7)== true){
          while (armControl.getEncoder().getPosition() > -200){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0);
              
      }
      if (stick.getRawButton(2) == true){
        while(armControl.getEncoder().getPosition() < 0){
            armControl.set(1);
            
        }
      }
          else {
            armControl.set(0);
            
          }*/
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putString("meme", Boolean.toString(meme));
    meme = tankStick.getRawButton(1);
    mememe = stick.getRawButton(1);
    
    e = -0.5*(stick.getRawAxis(3)-1);
    d = -0.5*(tankStick.getRawAxis(3)-1);
    if (tankStick.getRawButton(6) == true && tankStick.getRawButton(4) == false){
        pusher.set(DoubleSolenoid.Value.kForward);
       }
       if (tankStick.getRawButton(4) == true && tankStick.getRawButton(6) == false){
        pusher.set(DoubleSolenoid.Value.kReverse);
       }
    
    else {
      if (mememe == true){
        drive.arcadeDrive(stick.getRawAxis(2)*e,stick.getRawAxis(1)*d*-1 );
      }
      else{
        drive.arcadeDrive(stick.getRawAxis(2)*0.75*e,stick.getRawAxis(1)*d*-0.75 );
        
        
      }
      
    if (tankStick.getRawButton(3) == true ){
        intake.set(.25);
    }
     else if (tankStick.getRawButton(5) == true){
        intake.set(-.75);
    }
    else {
        intake.set(0);
    }
    //elevator code starts here
    if (tankStick.getRawButton(7) == true && wristControl.getEncoder().getPosition() > -300){
        wristControl.set(-1);
        elevatorControl.set(0);
    }
    else if (tankStick.getRawButton(7) == true && wristControl.getEncoder().getPosition() <= -300 && elevatorControl.getEncoder().getPosition() > -300){
        wristControl.set(0);
        elevatorControl.set(-1);
    }
    else {
        wristControl.set(0);
        elevatorControl.set(0);
    }
    if (tankStick.getRawButton(9) == true && elevatorControl.getEncoder().getPosition() < 0 && wristControl.getEncoder().getPosition() <= -300){
        wristControl.set(0);
        elevatorControl.set(1);
    }
    else if (tankStick.getRawButton(9) == true && wristControl.getEncoder().getPosition() < 0){
        wristControl.set(1);
        elevatorControl.set(0);
    }
    else {
        wristControl.set(0);
        elevatorControl.set(0);
    }
    
    }
  /*  if (tankStick.getRawButton(11)== true){
      while (wristControl.getEncoder().getPosition() > -233){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0);
          
  }
  if (tankStick.getRawButton(9)== true){
      while (wristControl.getEncoder().getPosition() > -467){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0); //hey
          
  }
  if (tankStick.getRawButton(7)== true){
      while (wristControl.getEncoder().getPosition() > -700){
          wristControl.set(-1);
          
      } 
  }
      else {
          wristControl.set(0);
          
  }
  if (tankStick.getRawButton(2) == true){
    while(wristControl.getEncoder().getPosition() < 0){
        wristControl.set(1);
        
    }
  }
      else {
        wristControl.set(0);
        
      }
      if (tankStick.getRawButton(12)== true){
        while (elevatorControl.getEncoder().getPosition() > -233){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0);
            
    }
    if (tankStick.getRawButton(10)== true){
        while (elevatorControl.getEncoder().getPosition() > -467){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0); //hey
            
    }
    if (tankStick.getRawButton(8)== true){
        while (elevatorControl.getEncoder().getPosition() > -700){
            elevatorControl.set(-1);
            
        } 
    }
        else {
            elevatorControl.set(0);
            
    }
    if (tankStick.getRawButton(3) == true){
      while(elevatorControl.getEncoder().getPosition() < 0){
          elevatorControl.set(1);
          
      }
    }
        else {
          elevatorControl.set(0);
          
        }
        if (stick.getRawButton(11)== true){
          while (armControl.getEncoder().getPosition() > -67){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0);
              
      }
      if (stick.getRawButton(9)== true){
          while (armControl.getEncoder().getPosition() > -133){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0); //hey
              
      }
      if (stick.getRawButton(7)== true){
          while (armControl.getEncoder().getPosition() > -200){
              armControl.set(-1);
              
          } 
      }
          else {
              armControl.set(0);
              
      }
      if (stick.getRawButton(2) == true){
        while(armControl.getEncoder().getPosition() < 0){
            armControl.set(1);
            
        }
      }
          else {
            armControl.set(0);
            
          }
    
    

  
*/

  }
   
  @Override
  public void testPeriodic() {
  }
}
