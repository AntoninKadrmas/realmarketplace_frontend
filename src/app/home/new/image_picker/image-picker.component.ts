import {Component, NgZone} from "@angular/core";
import {ImageAsset} from "@nativescript/core";
import * as imagePickerPlugin from '@nativescript/imagepicker';

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
    this.isSingleMode = false;

      let imagePicker = imagePickerPlugin.create({
      mode: 'multiple',
    });
    this.startSelection(imagePicker);
  }

  public onSelectSingleTap() {
    this.isSingleMode = true;

    let imagePicker = imagePickerPlugin.create({
      mode: 'single',
    });
    this.startSelection(imagePicker);
  }

  private startSelection(imagePicker) {
    imagePicker
      .authorize()
      .then(() => {
        this._ngZone.run(() => {
        });
        return imagePicker.present();
      })
      .then((selection) => {
        this._ngZone.run(() => {
          // set the images to be loaded from the assets with optimal sizes (optimize memory usage)
          selection.forEach((el: ImageAsset) => {
            el.options.width = this.thumbSize
            el.options.height = this.thumbSize
          });
          for(let value of selection){
            this.imageAssets.push(value)
            console.log(this.imageAssets.filter(x=>x==value).length)
            if(this.imageAssets.filter(x=>JSON.stringify(x)==JSON.stringify(value)).length>1)this.imageAssets.splice(this.imageAssets.length-1,1)
          }
        });
      })
      .catch(function (e) {
        console.log(e);
      });
  }
}
