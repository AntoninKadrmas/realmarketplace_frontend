import {Component, Input} from "@angular/core";
import {Book} from "../../assets/lists/book";

@Component({
  selector:'ns-item',
  templateUrl:'./item.component.html',
  styleUrls:['./item.component.scss']
})
export class ItemComponent{
  @Input() bookObject:Book ;
}
