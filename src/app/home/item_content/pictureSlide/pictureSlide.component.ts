import {Component, Input, OnInit} from "@angular/core";

@Component({
  selector:'ns-picture-slide-item',
  templateUrl:'./pictureSlide.component.html',
  styleUrls:['./pictureSlide.component.scss']
})
export class PictureSlideComponent{
  @Input() imageUrls:[];
}
