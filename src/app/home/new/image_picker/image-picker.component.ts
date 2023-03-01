import {Component, NgZone} from "@angular/core";
const RadImagepicker = require('@nstudio/nativescript-rad-imagepicker').RadImagepicker;
const PickerOptions = require('@nstudio/nativescript-rad-imagepicker').PickerOptions;

@Component({
  selector:'ns-image-picker',
  templateUrl:'./image-picker.component.html',
  styleUrls:['./image-picker.component.scss']
})
// @ts-ignore
export class ImagePickerComponent{
  imageAssets = [];
  isSingleMode: boolean = true;
  thumbSize: number = 100;

  constructor(private _ngZone: NgZone) {}

  public onSelectMultipleTap() {
    const radImagePicker = new RadImagepicker( );
    const options = new PickerOptions();
    options.imageLimit=10
    radImagePicker.pick(options).then((selectedImages) => {
      if (selectedImages) {
        console.log(selectedImages)
      }
    })
  }
}
